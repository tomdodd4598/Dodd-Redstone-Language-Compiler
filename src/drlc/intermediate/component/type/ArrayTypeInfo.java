package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class ArrayTypeInfo extends TypeInfo {
	
	public final @NonNull TypeInfo elementTypeInfo;
	public final int length;
	
	public final @NonNull TypeInfo decayTypeInfo;
	
	public ArrayTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull TypeInfo elementTypeInfo, int length) {
		super(node, referenceLevel);
		this.elementTypeInfo = elementTypeInfo;
		this.length = length;
		
		decayTypeInfo = elementTypeInfo.modifiedReferenceLevel(node, referenceLevel);
		
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
		
		if (length < 0) {
			throw Helpers.nodeError(node, "Length of array type \"%s\" can not be negative!", rawString());
		}
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new ArrayTypeInfo(node, newReferenceLevel, elementTypeInfo, length);
	}
	
	@Override
	public boolean exists(Scope scope) {
		return elementTypeInfo.exists(scope);
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : length * elementTypeInfo.getSize();
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (otherInfo instanceof ArrayTypeInfo) {
			@NonNull ArrayTypeInfo otherArrayInfo = (ArrayTypeInfo) otherInfo;
			if (referenceLevel == otherArrayInfo.referenceLevel && length == otherArrayInfo.length) {
				@NonNull TypeInfo otherElementInfo = otherArrayInfo.elementTypeInfo;
				if ((length == 0 && (elementTypeInfo.equals(Main.generator.wildcardPtrTypeInfo) || otherElementInfo.equals(Main.generator.wildcardPtrTypeInfo))) || elementTypeInfo.equals(otherElementInfo)) {
					return true;
				}
			}
		}
		return isAddress() && (otherInfo.equals(Main.generator.wildcardPtrTypeInfo) || decayTypeInfo.equals(otherInfo));
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return isAddress() ? decayTypeInfo : null;
	}
	
	@Override
	public boolean isArray() {
		return !isAddress();
	}
	
	@Override
	public void collectRawTypes(Set<RawType> rawTypes) {
		if (!isAddress()) {
			elementTypeInfo.collectRawTypes(rawTypes);
		}
	}
	
	@Override
	public int indexToOffsetShallow(ASTNode<?, ?> node, int index) {
		if (index >= length) {
			throw Helpers.nodeError(node, "Attempted to index array type \"%s\" at position %d!", this, index);
		}
		else {
			return index * elementTypeInfo.getSize();
		}
	}
	
	@Override
	public int offsetToIndexShallow(ASTNode<?, ?> node, int offset) {
		int index = offset / elementTypeInfo.getSize();
		if (index >= length) {
			throw Helpers.nodeError(node, "Attempted to index array type \"%s\" at position %d!", this, index);
		}
		else {
			return index;
		}
	}
	
	@Override
	public @NonNull TypeInfo atIndex(ASTNode<?, ?> node, int index) {
		return decayTypeInfo;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceLevel, elementTypeInfo, length);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof ArrayTypeInfo) {
			ArrayTypeInfo other = (ArrayTypeInfo) obj;
			boolean equalReferenceLevels = ignoreReferenceLevels || referenceLevel == other.referenceLevel;
			return equalReferenceLevels && elementTypeInfo.equals(other.elementTypeInfo) && length == other.length;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Global.ARRAY_START + elementTypeInfo + Global.ARRAY_TYPE_DELIMITER + " " + length + Global.ARRAY_END;
	}
}
