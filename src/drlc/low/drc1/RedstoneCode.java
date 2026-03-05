package drlc.low.drc1;

import java.util.*;
import java.util.stream.Collectors;

import drlc.Global;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.drc1.builtin.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.address.InstructionAddress;
import drlc.low.drc1.instruction.data.InstructionValueData;
import drlc.low.drc1.instruction.jump.InstructionJump;
import drlc.low.drc1.instruction.subroutine.*;

public class RedstoneCode extends LowCode<RedstoneCode, RedstoneRoutine, Instruction> {
	
	public boolean requiresStack = false;
	
	public final boolean mixedWidth;
	public final boolean textFirst;
	
	public int addressOffset = 0;
	
	public RedstoneCode(boolean mixedWidth, boolean textFirst) {
		super();
		this.mixedWidth = mixedWidth;
		this.textFirst = textFirst;
	}
	
	@Override
	protected RedstoneRoutine createRoutine(Routine intermediateRoutine) {
		return new RedstoneRoutine(this, intermediateRoutine);
	}
	
	@Override
	public RedstoneRoutine addRoutine(Function function, Routine intermediateRoutine) {
		RedstoneRoutine routine = super.addRoutine(function, intermediateRoutine);
		if (routine.isStackRoutine()) {
			requiresStack = true;
		}
		return routine;
	}
	
	@Override
	public boolean generate() {
		addRoutines();
		
		while (new ArrayList<>(routineMap.values()).stream().mapToInt(x -> x.generateInstructions() ? 1 : 0).sum() > 0) {
			;
		}
		
		optimize();
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.prepareDataInfoRegeneration();
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.regenerateDataInfo();
		}
		
		if (mixedWidth) {
			resetLongAddressInstructions();
			boolean changed = true;
			while (changed) {
				regenerateAddresses();
				changed = promoteLongAddressInstructions();
			}
		}
		else {
			regenerateAddresses();
			if (addressOffset > BYTE_MASK) {
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	protected RedstoneRoutine getBuiltInRoutine(String name, Routine intermediateRoutine) {
		switch (name) {
			case Global.PRINT_BOOL:
				return new PrintBoolRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_INT:
				return new PrintIntRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_NAT:
				return new PrintNatRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_CHAR:
				return new PrintCharRedstoneRoutine(this, intermediateRoutine);
			case Global.INT_LEFT_ROTATE_INT:
				return new IntLeftRotateIntRedstoneRoutine(this, intermediateRoutine);
			case Global.INT_RIGHT_ROTATE_INT:
				return new IntRightRotateIntRedstoneRoutine(this, intermediateRoutine);
			case Global.NAT_DIVIDE_NAT:
				return new NatDivideNatRedstoneRoutine(this, intermediateRoutine);
			case Global.NAT_REMAINDER_NAT:
				return new NatRemainderNatRedstoneRoutine(this, intermediateRoutine);
			case Global.NAT_RIGHT_SHIFT_INT:
				return new NatRightShiftIntRedstoneRoutine(this, intermediateRoutine);
			case Global.INT_COMPARE_INT:
				return new IntCompareIntRedstoneRoutine(this, intermediateRoutine);
			case Global.NAT_COMPARE_NAT:
				return new NatCompareNatRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_DIGITS:
				return new PrintDigitsRedstoneRoutine(this, intermediateRoutine);
			default:
				throw new IllegalArgumentException(String.format("Encountered unsupported built-in subroutine \"%s\"!", name));
		}
	}
	
	@Override
	protected void optimize() {
		for (RedstoneRoutine routine : routineMap.values()) {
			boolean flag = true;
			while (flag) {
				flag = RedstoneOptimization.removeNoOps(routine);
				flag |= RedstoneOptimization.removeDeadInstructions(routine);
				flag |= RedstoneOptimization.simplifyImmediateInstructions(routine);
				flag |= RedstoneOptimization.removeUnnecessaryLoads(routine);
				flag |= RedstoneOptimization.removeUnnecessaryStores(routine);
				flag |= RedstoneOptimization.removeUnusedTemporaryData(routine);
				flag |= RedstoneOptimization.removeUnnecessaryJumps(routine);
				flag |= RedstoneOptimization.simplifyConditionalJumps(routine);
				flag |= RedstoneOptimization.compressSuccessiveInstructions(routine);
			}
		}
	}
	
	protected void regenerateAddresses() {
		clearAddressingState();
		
		if (textFirst) {
			for (RedstoneRoutine routine : routineMap.values()) {
				routine.generateTextAddresses();
			}
			for (RedstoneRoutine routine : routineMap.values()) {
				routine.generateDataAddresses();
			}
		}
		else {
			for (RedstoneRoutine routine : routineMap.values()) {
				if (routine.isRootRoutine()) {
					routine.generateTextAddresses();
				}
			}
			for (RedstoneRoutine routine : routineMap.values()) {
				routine.generateDataAddresses();
			}
			for (RedstoneRoutine routine : routineMap.values()) {
				if (!routine.isRootRoutine()) {
					routine.generateTextAddresses();
				}
			}
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.finalizeInstructions();
		}
	}
	
	protected void clearAddressingState() {
		addressOffset = 0;
		textAddressMap.clear();
		rootAddressMap.clear();
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.sectionAddressMap.clear();
			routine.localAddressMap.clear();
			routine.tempAddressMap.clear();
		}
	}
	
	protected void resetLongAddressInstructions() {
		for (RedstoneRoutine routine : routineMap.values()) {
			for (List<Instruction> section : routine.sectionTextMap.values()) {
				for (Instruction instruction : section) {
					if (instruction instanceof InstructionAddress ia) {
						ia.longAddress = false;
					}
					else if (instruction instanceof InstructionJump ij) {
						ij.longAddress = false;
					}
					else if (instruction instanceof InstructionLoadSubroutineAddress ilsa) {
						ilsa.longAddress = false;
					}
					else if (instruction instanceof InstructionCallSubroutine ics) {
						ics.longAddress = false;
					}
				}
			}
		}
	}
	
	protected boolean promoteLongAddressInstructions() {
		boolean changed = false;
		for (RedstoneRoutine routine : routineMap.values()) {
			for (List<Instruction> section : routine.sectionTextMap.values()) {
				for (Instruction instruction : section) {
					if (instruction instanceof InstructionAddress ia) {
						if (!ia.longAddress && ia.address != null && isLong(ia.address)) {
							ia.longAddress = true;
							changed = true;
						}
					}
					else if (instruction instanceof InstructionJump ij) {
						if (!ij.longAddress && ij.address != null && isLong(ij.address)) {
							ij.longAddress = true;
							changed = true;
						}
					}
					else if (instruction instanceof InstructionLoadSubroutineAddress ilsa) {
						if (!ilsa.longAddress && ilsa.value != null && isLong(ilsa.value)) {
							ilsa.longAddress = true;
							changed = true;
						}
					}
					else if (instruction instanceof InstructionCallSubroutine ics) {
						if (!ics.longAddress && ics.returnAddress != null && isLong(ics.returnAddress)) {
							ics.longAddress = true;
							changed = true;
						}
					}
				}
			}
		}
		return changed;
	}
	
	public List<Instruction> getStaticDataInAddressOrder(boolean includeUninitialized) {
		List<Map.Entry<LowDataInfo, Instruction>> entries = new ArrayList<>(staticDataMap.entrySet());
		entries.sort(Comparator.comparingInt(x -> x.getKey().routine.getAddress(x.getKey())));
		
		if (!includeUninitialized) {
			return entries.stream().map(Map.Entry::getValue).collect(Collectors.toList());
		}
		
		Map<Integer, Instruction> instructionMap = new HashMap<>();
		for (Map.Entry<LowDataInfo, Instruction> entry : entries) {
			instructionMap.put(entry.getKey().routine.getAddress(entry.getKey()), entry.getValue());
		}
		
		List<LowAddressSlice> slices = new ArrayList<>(rootAddressMap.values());
		slices.sort(Comparator.comparingInt(x -> x.start));
		
		List<Instruction> instructionList = new ArrayList<>();
		InstructionValueData zeroData = new InstructionValueData(Arrays.asList((short) 0));
		for (LowAddressSlice slice : slices) {
			int current = slice.start, end = slice.start + slice.size;
			while (current < end) {
				Instruction instruction = instructionMap.get(current);
				if (instruction != null) {
					instructionList.add(instruction);
					current += instruction.size(mixedWidth);
				}
				else {
					instructionList.add(zeroData);
					++current;
				}
			}
		}
		return instructionList;
	}
	
	public List<Map.Entry<Integer, Instruction>> getInitializedStaticDataEntriesInAddressOrder() {
		List<Map.Entry<Integer, Instruction>> entries = new ArrayList<>();
		for (Map.Entry<LowDataInfo, Instruction> entry : staticDataMap.entrySet()) {
			entries.add(new AbstractMap.SimpleEntry<>(entry.getKey().routine.getAddress(entry.getKey()), entry.getValue()));
		}
		entries.sort(Comparator.comparingInt(Map.Entry::getKey));
		return entries;
	}
	
	// Static helpers
	
	public static final short BYTE_MASK = 0xFF;
	public static final short CHAR_MASK = 0x7F;
	public static final short SHIFT_MASK = 0xF;
	
	public static List<Short> raw(Value<?> value) {
		if (value instanceof BoolValue boolValue) {
			return Arrays.asList((short) (boolValue.value ? 1 : 0));
		}
		else if (value instanceof ArrayValue arrayValue) {
			return arrayValue.values.stream().flatMap(x -> raw(x).stream()).collect(Collectors.toList());
		}
		else if (value instanceof CompoundValue<?> compoundValue) {
			return compoundValue.values.stream().flatMap(x -> raw(x).stream()).collect(Collectors.toList());
		}
		else {
			return Arrays.asList(value.shortValue(null));
		}
	}
	
	public static boolean isLong(short value) {
		return value < 0 || value > BYTE_MASK;
	}
	
	public static short lowBits(short value) {
		return (short) (value & BYTE_MASK);
	}
	
	public static short charBits(short value) {
		return (short) (value & CHAR_MASK);
	}
	
	public static short shiftBits(short value) {
		return (short) (value & SHIFT_MASK);
	}
	
	public static boolean isPowerOfTwo(short value) {
		return value > 0 && ((value & (value - 1)) == 0);
	}
	
	public static short log2(short value) {
		if (value > 0) {
			int log = 0;
			if (value >= 256) {
				value >>>= 8;
				log += 8;
			}
			if (value >= 16) {
				value >>>= 4;
				log += 4;
			}
			if (value >= 4) {
				value >>>= 2;
				log += 2;
			}
			return (short) (log + (value >>> 1));
		}
		else {
			throw new IllegalArgumentException(String.format("Attempted to calculate logarithm of non-positive number %s!", value));
		}
	}
}
