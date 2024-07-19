package drlc.intermediate.component.value;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.NatTypeInfo;

public class NatValue extends WordValue<NatTypeInfo> {
	
	public NatValue(ASTNode<?> node, long value) {
		super(node, Main.generator.natTypeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NatValue) {
			return super.equals(obj);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Long.toUnsignedString(value);
	}
}
