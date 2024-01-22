package drlc.intermediate.component.value;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.ValueDataId;
import drlc.intermediate.component.type.TypeInfo;

public abstract class Value<T extends TypeInfo> {
	
	public final @NonNull T typeInfo;
	
	protected Value(ASTNode<?> node, @NonNull T typeInfo) {
		if (this instanceof AddressValue) {
			if (!typeInfo.isAddress()) {
				throw Helpers.nodeError(node, "Address value can not have non-address type \"%s\"!", typeInfo);
			}
		}
		else {
			if (typeInfo.isAddress()) {
				throw Helpers.nodeError(node, "Non-address value can not have address type \"%s\"!", typeInfo);
			}
		}
		this.typeInfo = typeInfo;
	}
	
	public @NonNull ValueDataId dataId() {
		return new ValueDataId(this);
	}
	
	public boolean boolValue(ASTNode<?> node) {
		throw Helpers.nodeError(node, "Value of type \"%s\" can not be cast to a Bool!", typeInfo);
	}
	
	public long longValue(ASTNode<?> node) {
		throw Helpers.nodeError(node, "Value of type \"%s\" can not be cast to an integer!", typeInfo);
	}
	
	public int intValue(ASTNode<?> node) {
		return (int) longValue(node);
	}
	
	public short shortValue(ASTNode<?> node) {
		return (short) longValue(node);
	}
	
	public byte byteValue(ASTNode<?> node) {
		return (byte) longValue(node);
	}
	
	public @NonNull Value<?> atIndex(ASTNode<?> node, int index) {
		throw Helpers.nodeError(node, "Value of type \"%s\" can not be indexed!", typeInfo);
	}
	
	public @NonNull Value<?> atOffset(ASTNode<?> node, int offset, @NonNull TypeInfo expectedTypeInfo) {
		if (offset == 0 && typeInfo.equalsOther(expectedTypeInfo, true) && typeInfo.getReferenceLevel() == expectedTypeInfo.getReferenceLevel()) {
			return this;
		}
		else {
			int index = typeInfo.offsetToIndexShallow(node, offset);
			return atIndex(node, index).atOffset(node, offset - typeInfo.indexToOffsetShallow(node, index), expectedTypeInfo);
		}
	}
	
	@Override
	public abstract int hashCode();
	
	@Override
	public abstract boolean equals(Object obj);
	
	public abstract String valueString();
	
	@Override
	public String toString() {
		return valueString() + Global.TYPE_ANNOTATION_PREFIX + ' ' + typeInfo;
	}
}
