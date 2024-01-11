package drlc.intermediate.component.value;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.BasicTypeInfo;

public abstract class WordValue<T extends BasicTypeInfo> extends BasicValue<T> {
	
	public final long value;
	
	public WordValue(ASTNode<?> node, @NonNull T typeInfo, long value) {
		super(node, typeInfo);
		this.value = value;
	}
	
	@Override
	public long longValue(ASTNode<?> node) {
		return value;
	}
	
	@Override
	public IntValue toInt(ASTNode<?> node) {
		return new IntValue(node, value);
	}
	
	@Override
	public NatValue toNat(ASTNode<?> node) {
		return new NatValue(node, value);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WordValue) {
			WordValue<?> other = (WordValue<?>) obj;
			return typeInfo.equals(other.typeInfo) && value == other.value;
		}
		else {
			return false;
		}
	}
}
