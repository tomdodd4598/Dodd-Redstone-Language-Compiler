package drlc.intermediate.component.value;

import java.util.Objects;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;

public class BoolValue extends BasicValue {
	
	public final boolean value;
	
	public BoolValue(ASTNode<?, ?> node, boolean value) {
		super(node, Main.generator.boolTypeInfo);
		this.value = value;
	}
	
	@Override
	public boolean boolValue(ASTNode<?, ?> node) {
		return value;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BoolValue) {
			BoolValue other = (BoolValue) obj;
			return typeInfo.equals(other.typeInfo) && value == other.value;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Boolean.toString(value);
	}
}
