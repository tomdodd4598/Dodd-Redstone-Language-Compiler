package drlc.interpret.component;

import drlc.interpret.component.info.type.TypeInfo;

public class Constant {
	
	public final String name;
	public final TypeInfo typeInfo;
	public final long value;
	
	public Constant(String name, TypeInfo typeInfo, long value) {
		this.name = name;
		this.typeInfo = typeInfo;
		this.value = value;
	}
}
