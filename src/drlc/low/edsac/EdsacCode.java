package drlc.low.edsac;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.data.DataId.LowDataId;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.edsac.builtin.*;
import drlc.low.edsac.instruction.Instruction;
import drlc.low.edsac.instruction.address.InstructionAddStackTargetOffset;
import drlc.low.edsac.instruction.data.*;

public class EdsacCode extends LowCode<EdsacCode, EdsacRoutine, Instruction> {
	
	public int addressOffset = 0;
	public long addressId = 0L;
	
	protected final Map<Object, DataId> utilityDataIdMap = new LinkedHashMap<>();
	
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
		
		while (new ArrayList<>(routineMap.values()).stream().mapToInt(x -> x.generateInstructions() ? 1 : 0).sum() > 0) {
			;
		}
		
		optimize();
		
		pruneStaticData();
		
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
			if (routine.isStackRoutine()) {
				routine.generateDataAddresses();
			}
		}
		
		generateStackTargetOffsets();
		
		for (EdsacRoutine routine : routineMap.values()) {
			if (!routine.isStackRoutine()) {
				routine.generateDataAddresses();
			}
		}
		
		prepareStaticData();
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.finalizeInstructions();
		}
		
		finalizeStaticData();
		
		return true;
	}
	
	@Override
	protected EdsacRoutine getBuiltInRoutine(String name, Routine intermediateRoutine) {
		switch (name) {
			case Global.READ_BOOL:
				return new ReadBoolEdsacRoutine(this, intermediateRoutine);
			case Global.READ_CHAR:
				return new ReadCharEdsacRoutine(this, intermediateRoutine);
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
				flag |= EdsacOptimization.removeDeadInstructions(routine);
				flag |= EdsacOptimization.simplifyImmediateInstructions(routine);
				flag |= EdsacOptimization.removeUnnecessaryLoads(routine);
				flag |= EdsacOptimization.removeUnnecessaryStores(routine);
				flag |= EdsacOptimization.removeUnusedTemporaryData(routine);
				flag |= EdsacOptimization.removeUnnecessaryJumps(routine);
				flag |= EdsacOptimization.simplifyConditionalJumps(routine);
				flag |= EdsacOptimization.compressSuccessiveInstructions(routine);
			}
		}
	}
	
	protected void pruneStaticData() {
		Set<LowDataId> liveStaticDataIds = new LinkedHashSet<>();
		Deque<LowDataId> queue = new ArrayDeque<>();
		
		for (EdsacRoutine routine : routineMap.values()) {
			for (List<Instruction> section : routine.sectionTextMap.values()) {
				for (Instruction instruction : section) {
					LowDataInfo dataInfo = instruction.getDataInfo();
					if (dataInfo != null) {
						addLiveStaticDataId(liveStaticDataIds, queue, dataInfo);
					}
				}
			}
		}
		
		while (!queue.isEmpty()) {
			LowDataId lowDataId = queue.removeFirst();
			for (Entry<LowDataInfo, Instruction> entry : staticDataMap.entrySet()) {
				if (entry.getKey().dataId.low().equals(lowDataId) && entry.getValue() instanceof InstructionAddressData addressData) {
					addLiveStaticDataId(liveStaticDataIds, queue, addressData.dataInfo);
				}
			}
		}
		
		Iterator<Entry<LowDataInfo, Instruction>> staticDataIter = staticDataMap.entrySet().iterator();
		while (staticDataIter.hasNext()) {
			Entry<LowDataInfo, Instruction> entry = staticDataIter.next();
			if (!liveStaticDataIds.contains(entry.getKey().dataId.low())) {
				staticDataIter.remove();
			}
		}
		
		Iterator<Entry<LowDataId, Pair<DataId, LowDataSpan>>> spanIter = rootSpanMap.entrySet().iterator();
		while (spanIter.hasNext()) {
			Entry<LowDataId, Pair<DataId, LowDataSpan>> entry = spanIter.next();
			if (!liveStaticDataIds.contains(entry.getKey())) {
				spanIter.remove();
			}
		}
		
		Set<LowDataSpan> liveStaticSpans = new HashSet<>();
		for (Pair<DataId, LowDataSpan> pair : rootSpanMap.values()) {
			liveStaticSpans.add(pair.right);
		}
		
		Iterator<Entry<LowDataSpan, LowAddressSlice>> addressIter = rootAddressMap.entrySet().iterator();
		while (addressIter.hasNext()) {
			Entry<LowDataSpan, LowAddressSlice> entry = addressIter.next();
			if (!liveStaticSpans.contains(entry.getKey())) {
				addressIter.remove();
			}
		}
	}
	
	private void addLiveStaticDataId(Set<LowDataId> liveStaticDataIds, Deque<LowDataId> queue, LowDataInfo info) {
		if (info != null && info.type == LowDataType.STATIC) {
			LowDataId lowDataId = info.dataId.low();
			if (liveStaticDataIds.add(lowDataId)) {
				queue.addLast(lowDataId);
			}
		}
	}
	
	protected DataId builtinDataId(Object key) {
		return utilityDataIdMap.computeIfAbsent(key, k -> Main.generator.nextGlobalDataId(Main.generator.intTypeInfo));
	}
	
	public DataId basePointerDataId() {
		return builtinDataId("BP");
	}
	
	public DataId stackPointerDataId() {
		return builtinDataId("SP");
	}
	
	public DataId clearDataId() {
		return builtinDataId("CLEAR");
	}
	
	public DataId scratchDataId(int index) {
		return builtinDataId(index);
	}
	
	protected boolean isScratchDataId(DataId dataId) {
		return utilityDataIdMap.entrySet().stream().anyMatch(x -> x.getKey() instanceof Integer && x.getValue().equals(dataId));
	}
	
	protected void prepareStaticData() {
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
		
		List<Entry<LowDataInfo, Instruction>> currentStaticDataEntryList = new ArrayList<>(staticDataMap.entrySet());
		currentStaticDataEntryList.sort(Comparator.comparingInt(x -> staticDataAddress(x.getKey())));
		
		int explicitEnd = currentStaticDataEntryList.stream().mapToInt(x -> staticDataAddress(x.getKey()) + x.getValue().size()).max().orElse(start);
		if (explicitEnd > end) {
			throw new IllegalArgumentException("Encountered entry past allocated memory in EDSAC static data!");
		}
		end = explicitEnd;
		
		Map<LowDataInfo, Instruction> nextStaticDataEntryList = new LinkedHashMap<>();
		int current = start, index = 0;
		while (current < end) {
			Entry<LowDataInfo, Instruction> entry = index < currentStaticDataEntryList.size() ? currentStaticDataEntryList.get(index) : null;
			int next = entry == null ? Integer.MAX_VALUE : staticDataAddress(entry.getKey());
			if (next < current) {
				throw new IllegalArgumentException("Encountered overlapping entries in EDSAC static data!");
			}
			
			if (entry != null && next == current) {
				Instruction instruction = entry.getValue();
				nextStaticDataEntryList.put(entry.getKey(), instruction);
				current += instruction.size();
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
	
	protected void finalizeStaticData() {
		for (Instruction data : staticDataMap.values()) {
			if (data instanceof InstructionAddressData iad) {
				iad.address = staticDataAddress(iad.dataInfo);
			}
			
			else if (data instanceof InstructionSubroutineAddressData isad) {
				int address = textAddressMap.get(isad.function);
				EdsacRoutine routine = routineMap.get(isad.function);
				if (routine != null && routine.isStackRoutine()) {
					address += routine.sectionAddressMap.get(-1);
				}
				isad.setValue(address);
			}
		}
	}
	
	protected void generateStackTargetOffsets() {
		for (EdsacRoutine routine : routineMap.values()) {
			for (List<Instruction> section : routine.sectionTextMap.values()) {
				for (Instruction instruction : section) {
					if (instruction instanceof InstructionAddStackTargetOffset iasto) {
						iasto.dataInfo = routine.constantDataInfo(routine.getAddress(iasto.stackTargetDataInfo));
					}
				}
			}
		}
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
