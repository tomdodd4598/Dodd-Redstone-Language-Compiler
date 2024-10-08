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
	public int hashCode() {
		return Objects.hash(typeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof WordValue<?> other) {
			return typeInfo.equals(other.typeInfo) && value == other.value;
		}
		else {
			return false;
		}
	}
}
