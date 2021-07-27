package drlc.interpret.routine;

import drlc.Global;

public enum RoutineType {
	
	LEAF(Global.LEAF),
	NESTING(Global.NESTING),
	RECURSIVE(Global.RECURSIVE);
	
	public final String str;
	
	private RoutineType(String str) {
		this.str = str;
	}
	
	public RoutineType onNesting() {
		return this == RECURSIVE ? RECURSIVE : NESTING;
	}
	
	public RoutineType onRecursion() {
		return RECURSIVE;
	}
}
