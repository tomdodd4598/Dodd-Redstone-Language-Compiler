package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.TypeDef;

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
		if (isAddress()) {
			return Main.generator.getAddressSize();
		}
		
		try {
			return Math.multiplyExact(length, elementTypeInfo.getSize());
		}
		catch (ArithmeticException e) {
			throw Helpers.error("Size of array type \"%s\" is too large!", rawString());
		}
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (otherInfo instanceof ArrayTypeInfo otherArrayInfo) {
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
		if (index < 0 || index >= length) {
			throw Helpers.nodeError(node, "Attempted to index array type \"%s\" at position %d!", this, index);
		}
		else {
			try {
				return Math.multiplyExact(index, elementTypeInfo.getSize());
			}
			catch (ArithmeticException e) {
				throw Helpers.nodeError(node, "Offset of array type \"%s\" at position %d is too large!", this, index);
			}
		}
	}
	
	@Override
	public int offsetToIndexShallow(ASTNode<?> node, int offset) {
		if (offset < 0) {
			throw Helpers.nodeError(node, "Attempted to index array type \"%s\" at position %d!", this, offset);
		}
		
		int elementSize = elementTypeInfo.getSize();
		if (elementSize == 0) {
			throw Helpers.nodeError(node, "Can not index array type \"%s\" with zero-sized element type \"%s\"!", this, elementTypeInfo);
		}
		
		int index = offset / elementSize;
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
		if (obj instanceof ArrayTypeInfo other) {
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
