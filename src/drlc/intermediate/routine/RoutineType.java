package drlc.intermediate.routine;

import drlc.Global;

public enum RoutineType {
	
	LEAF(Global.LEAF),
	NESTING(Global.NESTING),
	STACK(Global.STACK);
	
	private final String str;
	
	private RoutineType(String str) {
		this.str = str;
	}
	
	public RoutineType onNesting() {
		return this == STACK ? STACK : NESTING;
	}
	
	public RoutineType onRecursion() {
		return STACK;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
