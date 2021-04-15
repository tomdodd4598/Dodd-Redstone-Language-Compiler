package drlc.generate.drc1;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import drlc.generate.drc1.instruction.Instruction;
import drlc.interpret.Program;
import drlc.interpret.routine.Routine;

public class RedstoneCode {
	
	public final Program program;
	public boolean requiresStack = false;
	
	public final Map<Integer, Instruction> argDataMap = new TreeMap<>();
	public final Map<String, RedstoneRoutine> routineMap = new LinkedHashMap<>();
	
	public final Map<String, Integer> staticIdMap = new LinkedHashMap<>();
	
	public short addressOffset = 0;
	public final Map<String, Short> textAddressMap = new HashMap<>();
	public final Map<Integer, Short> staticAddressMap = new HashMap<>();
	
	public RedstoneCode(Program program) {
		this.program = program;
	}
	
	public void generate(boolean optimize) {
		for (Entry<String, Routine> entry : program.getRoutineMap().entrySet()) {
			RedstoneRoutine routine = new RedstoneRoutine(this, entry.getValue());
			routineMap.put(entry.getKey(), routine);
			if (routine.isRecursive()) {
				requiresStack = true;
			}
		}
		
		for (RedstoneRoutine routine : routineMap.values()) {
			routine.generateInstructions();
		}
		
		if (optimize) {
			optimize();
		}
		
		staticIdMap.clear();
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
	
	public void optimize() {
		for (RedstoneRoutine routine : routineMap.values()) {
			boolean flag = true;
			while (flag) {
				flag = RedstoneOptimization.removeNoOps(routine);
				flag |= RedstoneOptimization.removeUnnecessaryImmediates(routine);
				flag |= RedstoneOptimization.removeUnnecessaryConstants(routine);
				flag |= RedstoneOptimization.removeUnnecessaryLoads(routine);
				flag |= RedstoneOptimization.removeUnnecessaryStores(routine);
				flag |= RedstoneOptimization.removeUnnecessaryJumps(routine);
				flag |= RedstoneOptimization.simplifyConditionalJumps(routine);
				flag |= RedstoneOptimization.simplifyALUImmediateInstructions(routine);
			}
		}
	}
	
	// Static helpers
	
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
