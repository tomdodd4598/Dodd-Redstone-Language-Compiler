package drlc.interpret.routine;

import drlc.Global;
import drlc.interpret.Program;
import drlc.interpret.type.VariableReferenceInfo;

public class RootRoutine extends Routine {
	
	public VariableReferenceInfo[] args = new VariableReferenceInfo[0];
	
	public RootRoutine(Program program) {
		super(program, Global.ROOT_ROUTINE);
		called = true;
		getDestructionActionList().add(Global.HALT_ROUTINE);
	}
	
	@Override
	public RoutineType getType() {
		RoutineType type = super.getType();
		if (type == RoutineType.RECURSIVE) {
			throw new IllegalArgumentException(String.format("Root routine can not be recursive!"));
		}
		return type;
	}
	
	@Override
	public void onNestingRoutine() {
		type = type.onNesting();
	}
	
	@Override
	protected void onRecursiveRoutine() {
		throw new IllegalArgumentException(String.format("Root routine can not be recursive!"));
	}
	
	@Override
	public String toString() {
		return name;
	}
}
