package drlc.intermediate.component.value;

import java.util.Objects;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;

public class VoidValue extends BasicValue {
	
	public VoidValue(ASTNode node) {
		super(node, Main.generator.voidTypeInfo);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VoidValue) {
			VoidValue other = (VoidValue) obj;
			return typeInfo.equals(other.typeInfo);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return "()";
	}
}
