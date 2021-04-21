package drlc.interpret.routine;

import drlc.interpret.Program;
import drlc.interpret.type.VariableReferenceInfo;

public abstract class Subroutine extends Routine {
	
	public final VariableReferenceInfo[] params;
	
	public Subroutine(Program program, String name, VariableReferenceInfo[] params) {
		super(program, name);
		this.params = params;
	}
	
	@Override
	public void onNestingRoutine() {
		type = type.onNesting();
	}
	
	@Override
	protected void onRecursiveRoutine() {
		type = type.onRecursion();
	}
}
