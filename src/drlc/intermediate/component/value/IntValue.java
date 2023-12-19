package drlc.intermediate.component.value;

import java.util.Objects;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;

public class IntValue extends BasicValue {
	
	public final long value;
	
	public IntValue(ASTNode<?, ?> node, long value) {
		super(node, Main.generator.intTypeInfo);
		this.value = value;
	}
	
	@Override
	public long longValue(ASTNode<?, ?> node) {
		return value;
	}
	
	@Override
	public IntValue toInt(ASTNode<?, ?> node) {
		return this;
	}
	
	@Override
	public NatValue toNat(ASTNode<?, ?> node) {
		return new NatValue(node, value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof IntValue) {
			IntValue other = (IntValue) obj;
			return typeInfo.equals(other.typeInfo) && value == other.value;
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
