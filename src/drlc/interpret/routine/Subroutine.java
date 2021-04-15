package drlc.interpret.routine;

import drlc.interpret.Program;

public abstract class Subroutine extends Routine {
	
	public final String[] params;
	
	public Subroutine(Program program, String name, String[] params) {
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
