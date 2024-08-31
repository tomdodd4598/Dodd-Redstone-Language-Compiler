package drlc.low.edsac;

import java.util.*;
import java.util.stream.Collectors;

import drlc.Global;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.low.LowCode;
import drlc.low.edsac.instruction.Instruction;

public class EdsacCode extends LowCode<EdsacCode, EdsacRoutine, Instruction> {
	
	public int addressOffset = 0;
	
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
			routine.generateDataIds();
		}
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.generateTextAddresses();
		}
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.generateDataAddresses();
		}
		
		for (EdsacRoutine routine : routineMap.values()) {
			routine.finalizeInstructions();
		}
		
		return true;
	}
	
	@Override
	protected EdsacRoutine getBuiltInRoutine(String name, Routine intermediateRoutine) {
		switch (name) {
			case Global.PRINT_BOOL:
				return new PrintBoolEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_INT:
				return new PrintIntEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_NAT:
				return new PrintNatEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_CHAR:
				return new PrintCharEdsacRoutine(this, intermediateRoutine);
			case Global.NAT_RIGHT_SHIFT_INT:
				return new NatRightShiftIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_LEFT_ROTATE_INT:
				return new IntLeftRotateIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_RIGHT_ROTATE_INT:
				return new IntRightRotateIntEdsacRoutine(this, intermediateRoutine);
			case Global.INT_COMPARE_INT:
				return new IntCompareIntEdsacRoutine(this, intermediateRoutine);
			case Global.NAT_COMPARE_NAT:
				return new NatCompareNatEdsacRoutine(this, intermediateRoutine);
			case Global.PRINT_DIGITS:
				return new PrintDigitsEdsacRoutine(this, intermediateRoutine);
		}
		throw new IllegalArgumentException(String.format("Encountered unsupported built-in subroutine \"%s\"!", name));
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
	
	// Static helpers
	
	public static List<EdsacInt> raw(Value<?> value) {
		if (value instanceof BoolValue boolValue) {
			return Arrays.asList(EdsacInt.of(boolValue.value ? 1 : 0));
		}
		else if (value instanceof CharValue charValue) {
			return Arrays.asList(EdsacInt.fromChar(charValue.value));
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
	
	public static final int ADDRESS_MASK = 0x3FF;
	
	public static int addressBits(int value) {
		return value & ADDRESS_MASK;
	}
}
