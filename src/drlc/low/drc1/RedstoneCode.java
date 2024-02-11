package drlc.low.drc1;

import java.util.*;
import java.util.stream.Collectors;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.data.DataId.LowDataId;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.low.*;
import drlc.low.drc1.builtin.*;

public class RedstoneCode {
	
	public boolean requiresStack = false;
	
	public final Map<Function, RedstoneRoutine> routineMap = new LinkedHashMap<>();
	
	public final Map<LowDataId, Pair<DataId, LowDataSpan>> rootSpanMap = new LinkedHashMap<>();
	
	public short addressOffset = 0;
	public final Map<LowDataSpan, LowAddressSlice> rootAddressMap = new LinkedHashMap<>();
	
	public final Map<Function, Short> textAddressMap = new LinkedHashMap<>();
	
	public boolean routineExists(Function function) {
		return routineMap.containsKey(function);
	}
	
	public RedstoneRoutine getRoutine(Function function) {
		return routineMap.get(function);
	}
	
	public void addRoutine(Function function, Routine intermediateRoutine) {
		RedstoneRoutine routine = new RedstoneRoutine(this, intermediateRoutine);
		routineMap.put(function, function.builtIn ? getBuiltInRoutine(function.name, intermediateRoutine) : routine);
		if (routine.isStackRoutine()) {
			requiresStack = true;
		}
	}
	
	public void generate() {
		Main.rootScope.routineMap.forEach((k, v) -> {
			if (!k.builtIn) {
				addRoutine(k, v);
			}
		});
		
		Main.rootScope.routineMap.forEach((k, v) -> {
			if (k.builtIn) {
				addRoutine(k, v);
			}
		});
		
		for (Function function : Main.rootScope.routineMap.keySet()) {
			if (!routineMap.containsKey(function)) {
				throw new IllegalArgumentException(String.format("Unexpectedly encountered unimplemented routine \"%s\"!", function));
			}
		}
		
		boolean flag = true;
		while (flag) {
			flag = false;
			for (RedstoneRoutine routine : new ArrayList<>(routineMap.values())) {
				flag |= routine.generateInstructions();
			}
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
	
	private RedstoneRoutine getBuiltInRoutine(String name, Routine intermediateRoutine) {
		switch (name) {
			case Global.PRINT_BOOL:
				return new PrintBoolRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_INT:
				return new PrintIntRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_NAT:
				return new PrintNatRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_CHAR:
				return new PrintCharRedstoneRoutine(this, intermediateRoutine);
			case Global.NAT_RIGHT_SHIFT_INT:
				return new NatRightShiftIntRedstoneRoutine(this, intermediateRoutine);
			case Global.INT_LEFT_ROTATE_INT:
				return new IntLeftRotateIntRedstoneRoutine(this, intermediateRoutine);
			case Global.INT_RIGHT_ROTATE_INT:
				return new IntRightRotateIntRedstoneRoutine(this, intermediateRoutine);
			case Global.INT_COMPARE_INT:
				return new IntCompareIntRedstoneRoutine(this, intermediateRoutine);
			case Global.NAT_COMPARE_NAT:
				return new NatCompareNatRedstoneRoutine(this, intermediateRoutine);
			case Global.PRINT_DIGITS:
				return new PrintDigitsRedstoneRoutine(this, intermediateRoutine);
		}
		throw new IllegalArgumentException(String.format("Encountered unsupported built-in subroutine \"%s\"!", name));
	}
	
	private void optimize() {
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
	public static final short CHAR_MASK = 0x7F;
	
	public static List<Short> raw(Value<?> value) {
		if (value instanceof BoolValue) {
			return Arrays.asList((short) (((BoolValue) value).value ? 1 : 0));
		}
		else if (value instanceof ArrayValue) {
			return ((ArrayValue) value).values.stream().flatMap(x -> raw(x).stream()).collect(Collectors.toList());
		}
		else if (value instanceof CompoundValue) {
			return ((CompoundValue<?>) value).values.stream().flatMap(x -> raw(x).stream()).collect(Collectors.toList());
		}
		else {
			return Arrays.asList(value.shortValue(null));
		}
	}
	
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
