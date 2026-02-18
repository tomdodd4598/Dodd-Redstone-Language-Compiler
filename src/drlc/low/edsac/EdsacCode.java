package drlc.low.edsac;

import java.util.*;
import java.util.stream.Collectors;

import drlc.Global;
import drlc.Helpers.Pair;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.edsac.builtin.*;
import drlc.low.edsac.instruction.Instruction;
import drlc.low.edsac.instruction.data.InstructionValueData;

public class EdsacCode extends LowCode<EdsacCode, EdsacRoutine, Instruction> {
	
	public int addressOffset = 0;
	public long addressId = 0L;
	
	public EdsacCode() {
		super();
	}
	
	@Override
	protected EdsacRoutine createRoutine(Routine intermediateRoutine) {
		return new EdsacRoutine(this, intermediateRoutine);
	}
	
	@Override
	public boolean generate() {
		addRoutines();
		
		while (new ArrayList<>(routineMap.values()).stream().mapToInt(x -> x.generateInstructions() ? 1 : 0).sum() > 0);
		
		optimize();
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.prepareDataInfoRegeneration();
		}
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.regenerateDataInfo();
		}
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.generateTextAddresses();
		}
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.generateDataAddresses();
		}
		
		finalizeStaticData();
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.finalizeInstructions();
		}
		
		return true;
	}
	
	@Override
	protected EdsacRoutine getBuiltInRoutine(String name, Routine intermediateRoutine) {
		switch (name) {
			case Global.READ_BOOL:
				return new ReadBoolEdsacRoutine(this, intermediateRoutine);
			case Global.READ_INT:
			case Global.READ_NAT:
				return new ReadIntEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_BOOL:
				return new PrintBoolEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_INT:
				return new PrintIntEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_NAT:
				return new PrintNatEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_CHAR:
				return new PrintCharEdsacRoutine(this, intermediateRoutine);
			case Global.INT_NOT_EQUAL_TO_INT:
				return new IntNotEqualToIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_LESS_THAN_INT:
				return new IntLessThanIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_LEFT_SHIFT_INT:
				return new IntLeftShiftIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_RIGHT_SHIFT_INT:
				return new IntRightShiftIntEdsacRoutine(this, intermediateRoutine);
			case Global.NAT_RIGHT_SHIFT_INT:
				return new NatRightShiftIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_LEFT_ROTATE_INT:
				return new IntLeftRotateIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_RIGHT_ROTATE_INT:
				return new IntRightRotateIntEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_DIGITS:
				return new PrintDigitsEdsacRoutine(this, intermediateRoutine);
			default:
				throw new IllegalArgumentException(String.format("Encountered unsupported built-in subroutine \"%s\"!", name));
		}
	}
	
	@Override
	protected void optimize() {
		for (EdsacRoutine routine : routineMap.values()) {
			boolean flag = true;
			while (flag) {
				flag = EdsacOptimization.removeNoOps(routine);
				// flag |= EdsacOptimization.removeDeadInstructions(routine);
				// flag |= EdsacOptimization.simplifyImmediateInstructions(routine);
				// flag |= EdsacOptimization.removeUnnecessaryLoads(routine);
				// flag |= EdsacOptimization.removeUnnecessaryStores(routine);
				// flag |= EdsacOptimization.removeUnusedTemporaryData(routine);
				// flag |= EdsacOptimization.removeUnnecessaryJumps(routine);
				// flag |= EdsacOptimization.simplifyConditionalJumps(routine);
				// flag |= EdsacOptimization.compressSuccessiveInstructions(routine);
			}
		}
	}
	
	protected void finalizeStaticData() {
		if (rootAddressMap.isEmpty() || staticDataMap.isEmpty()) {
			return;
		}
		
		Map<Integer, LowDataInfo> addressInfoMap = new HashMap<>();
		int start = Integer.MAX_VALUE, end = Integer.MIN_VALUE;
		for (Pair<DataId, LowDataSpan> pair : rootSpanMap.values()) {
			LowAddressSlice slice = rootAddressMap.get(pair.right);
			start = Math.min(start, slice.start);
			end = Math.max(end, slice.start + slice.size);
			for (int i = 0; i < slice.size; ++i) {
				addressInfoMap.put(slice.start + i, new LowDataInfo(this, pair.left, i, pair.right, LowDataType.STATIC));
			}
		}
		if (start == Integer.MAX_VALUE) {
			return;
		}
		
		List<Map.Entry<LowDataInfo, Instruction>> currentStaticDataEntryList = new ArrayList<>(staticDataMap.entrySet());
		currentStaticDataEntryList.sort(Comparator.comparingInt(x -> dataAddress(x.getKey())));
		
		int explicitEnd = currentStaticDataEntryList.stream().mapToInt(x -> dataAddress(x.getKey()) + x.getValue().size()).max().orElse(start);
		if (explicitEnd > end) {
			throw new IllegalArgumentException("Encountered entry past allocated memory in EDSAC static data!");
		}
		end = explicitEnd;
		
		LinkedHashMap<LowDataInfo, Instruction> nextStaticDataEntryList = new LinkedHashMap<>();
		int current = start, index = 0;
		while (current < end) {
			Map.Entry<LowDataInfo, Instruction> entry = index < currentStaticDataEntryList.size() ? currentStaticDataEntryList.get(index) : null;
			int next = entry == null ? Integer.MAX_VALUE : dataAddress(entry.getKey());
			if (next < current) {
				throw new IllegalArgumentException("Encountered overlapping entries in EDSAC static data!");
			}
			
			if (entry != null && next == current) {
				Instruction data = entry.getValue();
				nextStaticDataEntryList.put(entry.getKey(), data);
				current += data.size();
				++index;
			}
			else {
				LowDataInfo info = addressInfoMap.get(current);
				if (info == null) {
					throw new IllegalArgumentException("Encountered missing address span in EDSAC static data!");
				}
				nextStaticDataEntryList.put(info, new InstructionValueData(Arrays.asList(EdsacInt.ZERO)));
				++current;
			}
		}
		
		staticDataMap.clear();
		staticDataMap.putAll(nextStaticDataEntryList);
	}
	
	protected int dataAddress(LowDataInfo dataInfo) {
		LowAddressSlice slice = rootAddressMap.get(dataInfo.span);
		return slice.start + dataInfo.offset;
	}
	
	// Static helpers
	
	public static final long ARGUMENT_MASK = 0x7FFL;
	
	public static List<EdsacInt> raw(Value<?> value) {
		if (value instanceof BoolValue boolValue) {
			return Arrays.asList(boolValue.value ? EdsacInt.MINUS : EdsacInt.ZERO);
		}
		else if (value instanceof CharValue charValue) {
			return Arrays.asList(EdsacChar.of(charValue.value).toInt());
		}
		else if (value instanceof ArrayValue arrayValue) {
			return arrayValue.values.stream().flatMap(x -> raw(x).stream()).collect(Collectors.toList());
		}
		else if (value instanceof CompoundValue<?> compoundValue) {
			return compoundValue.values.stream().flatMap(x -> raw(x).stream()).collect(Collectors.toList());
		}
		else {
			return Arrays.asList(EdsacInt.of(value.longValue(null)));
		}
	}
	
	public static String instructionArgument(long value) {
		value &= ARGUMENT_MASK;
		return value == 0L ? "" : Long.toString(value);
	}
	
	public static long shiftBits(long value) {
		return Math.min(EdsacInt.BITS, value & Long.MAX_VALUE);
	}
	
	public static long rotationBits(long value) {
		return (value & Long.MAX_VALUE) % EdsacInt.BITS;
	}
}
