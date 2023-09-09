package drlc.low.drc1;

import java.util.*;
import java.util.Map.Entry;

import drlc.Global;
import drlc.intermediate.Program;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.*;
import drlc.low.drc1.builtin.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.immediate.InstructionLoadLongImmediate;

public class RedstoneCode {
	
	public final RedstoneGenerator generator;
	public final Program program;
	
	public boolean requiresStack = false;
	
	private final Map<String, RedstoneRoutine> routineMap = new LinkedHashMap<>();
	
	public final Set<String> unusedBuiltInRoutineSet;
	
	public final Map<DataId, Long> rootIdMap = new LinkedHashMap<>();
	
	public short addressOffset = 0;
	public final Map<RedstoneAddressKey, Short> rootAddressMap = new HashMap<>();
	
	public final Map<String, Short> textAddressMap = new HashMap<>();
	
	public RedstoneCode(RedstoneGenerator generator, Program program) {
		this.generator = generator;
		this.program = program;
		unusedBuiltInRoutineSet = new HashSet<>(program.builtInRoutineMap.keySet());
	}
	
	public Map<String, RedstoneRoutine> getRoutineMap() {
		return routineMap;
	}
	
	public RedstoneRoutine getRoutine(String name) {
		return routineMap.get(name);
	}
	
	public boolean routineExists(String name) {
		return routineMap.containsKey(name);
	}
	
	public void generate() {
		for (Entry<String, Routine> entry : program.routineMap.entrySet()) {
			Routine intermediateRoutine = entry.getValue();
			RedstoneRoutine routine = new RedstoneRoutine(this, intermediateRoutine);
			if (!intermediateRoutine.isBuiltInFunctionRoutine()) {
				routineMap.put(entry.getKey(), routine);
			}
			if (routine.isStackRoutine()) {
				requiresStack = true;
			}
		}
		
		addBuiltInRoutines();
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.generateInstructions();
		}
		
		optimize();
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.prepareDataIdRegeneration();
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.regenerateDataIds();
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.generateTextAddresses();
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.generateDataAddresses();
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.finalizeInstructions();
		}
	}
	
	private void addBuiltInRoutines() {
		routineMap.put(Global.OUTCHAR, new OutCharRedstoneRoutine(this, Global.OUTCHAR));
		routineMap.put(Global.OUTINT, new OutIntRedstoneRoutine(this, Global.OUTINT));
		routineMap.put(Global.ARGV_FUNCTION, new ArgvRedstoneRoutine(this, Global.ARGV_FUNCTION));
		
		routineMap.put(Global.LOGICAL_RIGHT_SHIFT, new LogicalRightShiftRedstoneRoutine(this, Global.LOGICAL_RIGHT_SHIFT));
		routineMap.put(Global.CIRCULAR_LEFT_SHIFT, new CircularLeftShiftRedstoneRoutine(this, Global.CIRCULAR_LEFT_SHIFT, RoutineCallType.NESTING));
		routineMap.put(Global.CIRCULAR_RIGHT_SHIFT, new CircularRightShiftRedstoneRoutine(this, Global.CIRCULAR_RIGHT_SHIFT, RoutineCallType.NESTING));
		
		for (String name : program.builtInRoutineMap.keySet()) {
			if (!routineMap.containsKey(name)) {
				throw new IllegalArgumentException(String.format("Unexpectedly encountered unimplemented built-in function \"%s\"!", name));
			}
		}
	}
	
	private void optimize() {
		for (String builtInRoutine : unusedBuiltInRoutineSet) {
			routineMap.remove(builtInRoutine);
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			boolean flag = true;
			while (flag) {
				flag = RedstoneOptimization.removeNoOps(routine);
				flag |= RedstoneOptimization.checkConstants(routine);
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
	
	// Static helpers
	
	public static final short MAX_ADDRESS = 0xFF;
	
	public static final Instruction LOAD_MIN_VALUE = new InstructionLoadLongImmediate(Short.MIN_VALUE);
	public static final Instruction LOAD_MIN_VALUE_SUCCEEDING = LOAD_MIN_VALUE.succeedingData();
	
	public static boolean isLongImmediate(short value) {
		return value < 0 || value > 0xFF;
	}
	
	public static short lowBits(short value) {
		return (short) (value & 0xFF);
	}
	
	public static short shiftBits(short value) {
		return (short) (value & 0xF);
	}
	
	public static boolean isPowerOfTwo(short value) {
		if (value < 0) {
			return ((-value) & (-value - 1)) == 0;
		}
		else {
			return value > 0 && ((value & (value - 1)) == 0);
		}
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
