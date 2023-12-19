package drlc.intermediate.component.value;

import java.util.Objects;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;

public class NatValue extends BasicValue {
	
	public final long value;
	
	public NatValue(ASTNode<?, ?> node, long value) {
		super(node, Main.generator.natTypeInfo);
		this.value = value;
	}
	
	@Override
	public long longValue(ASTNode<?, ?> node) {
		return value;
	}
	
	@Override
	public IntValue toInt(ASTNode<?, ?> node) {
		return new IntValue(node, value);
	}
	
	@Override
	public NatValue toNat(ASTNode<?, ?> node) {
		return this;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NatValue) {
			NatValue other = (NatValue) obj;
			return typeInfo.equals(other.typeInfo) && value == other.value;
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
