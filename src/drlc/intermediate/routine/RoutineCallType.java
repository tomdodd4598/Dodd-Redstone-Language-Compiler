package drlc.intermediate.routine;

import drlc.Global;

public enum RoutineCallType {
	
	LEAF(Global.LEAF),
	NESTING(Global.NESTING),
	STACK(Global.STACK);
	
	private final String str;
	
	private RoutineCallType(String str) {
		this.str = str;
	}
	
	public RoutineCallType onRequiresNesting() {
		return this == STACK ? STACK : NESTING;
	}
	
	public RoutineCallType onRequiresRecursion() {
		return STACK;
	}
	
	@Override
	public String toString() {
		return str;
	}
}
