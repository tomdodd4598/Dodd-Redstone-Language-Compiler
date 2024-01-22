package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;

public class ArrayTypeInfo extends TypeInfo {
	
	public final @NonNull TypeInfo elementTypeInfo;
	public final int length;
	
	public final @NonNull TypeInfo decayTypeInfo;
	
	public ArrayTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull TypeInfo elementTypeInfo, int length) {
		super(node, referenceMutability);
		this.elementTypeInfo = elementTypeInfo;
		this.length = length;
		
		decayTypeInfo = elementTypeInfo.addressOf(node, referenceMutability);
		
		if (length < 0) {
			throw Helpers.nodeError(node, "Length of array type \"%s\" can not be negative!", rawString());
		}
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new ArrayTypeInfo(node, referenceMutability, elementTypeInfo, length);
	}
	
	@Override
	public boolean isArray() {
		return !isAddress();
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : length * elementTypeInfo.getSize();
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (otherInfo instanceof ArrayTypeInfo) {
			@NonNull ArrayTypeInfo otherArrayInfo = (ArrayTypeInfo) otherInfo;
			if (length == otherArrayInfo.length && elementTypeInfo.canImplicitCastTo(otherArrayInfo.elementTypeInfo) && canImplicitCastToReferenceMutability(otherInfo)) {
				return true;
			}
		}
		return isAddress() && decayTypeInfo.canImplicitCastTo(otherInfo);
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {
		if (!isAddress()) {
			elementTypeInfo.collectTypeDefs(typeDefs);
		}
	}
	
	@Override
	public int indexToOffsetShallow(ASTNode<?> node, int index) {
		if (index >= length) {
			throw Helpers.nodeError(node, "Attempted to index array type \"%s\" at position %d!", this, index);
		}
		else {
			return index * elementTypeInfo.getSize();
		}
	}
	
	@Override
	public int offsetToIndexShallow(ASTNode<?> node, int offset) {
		int index = offset / elementTypeInfo.getSize();
		if (index >= length) {
			throw Helpers.nodeError(node, "Attempted to index array type \"%s\" at position %d!", this, index);
		}
		else {
			return index;
		}
	}
	
	@Override
	public @NonNull TypeInfo atIndex(ASTNode<?> node, int index) {
		return decayTypeInfo;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, elementTypeInfo, length);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof ArrayTypeInfo) {
			ArrayTypeInfo other = (ArrayTypeInfo) obj;
			boolean equalReferenceMutability = ignoreReferenceMutability || referenceMutability.equals(other.referenceMutability);
			return equalReferenceMutability && elementTypeInfo.equals(other.elementTypeInfo) && length == other.length;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Global.ARRAY_START + elementTypeInfo + Global.ARRAY_TYPE_DELIMITER + " " + length + Global.ARRAY_END;
	}
	
	@Override
	public String rawRoutineString() {
		return Global.ARRAY_START + elementTypeInfo.routineString() + Global.ARRAY_TYPE_DELIMITER + " " + length + Global.ARRAY_END;
	}
}
