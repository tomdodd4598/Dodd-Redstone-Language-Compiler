package drlc.intermediate.component;

import drlc.intermediate.component.value.Value;

public abstract class Directive {
	
	public final int argc;
	public final DeclaratorInfo[] params;
	
	public Directive(int argc, DeclaratorInfo[] params) {
		this.argc = argc;
		this.params = params;
	}
	
	public void call(Value[] values) {}
}
