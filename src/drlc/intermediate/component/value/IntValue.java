package drlc.intermediate.component.value;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.IntTypeInfo;

public class IntValue extends WordValue<IntTypeInfo> {
	
	public final long value;
	
	public IntValue(ASTNode<?> node, long value) {
		super(node, Main.generator.intTypeInfo, value);
		this.value = value;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntValue) {
			return super.equals(obj);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Long.toString(value);
	}
}
