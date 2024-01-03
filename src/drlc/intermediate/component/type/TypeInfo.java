package drlc.intermediate.component.type;

import java.util.Set;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.scope.Scope;

public abstract class TypeInfo {
	
	public final int referenceLevel;
	
	protected TypeInfo(ASTNode<?, ?> node, int referenceLevel) {
		this.referenceLevel = referenceLevel;
	}
	
	public abstract @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel);
	
	public @NonNull TypeInfo modifiedReferenceLevel(ASTNode<?, ?> node, int referenceLevelDiff) {
		return copy(node, referenceLevel + referenceLevelDiff);
	}
	
	public abstract boolean exists(Scope scope);
	
	public boolean isAddress() {
		return referenceLevel > 0;
	}
	
	public boolean isWord() {
		return false;
	}
	
	public abstract int getSize();
	
	public int getAddressOffsetSize(ASTNode<?, ?> node) {
		return modifiedReferenceLevel(node, -1).getSize();
	}
	
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (equals(otherInfo)) {
			return true;
		}
		else {
			return isAddress() && otherInfo.equals(Main.generator.wildcardPtrTypeInfo);
		}
	}
	
	public @Nullable TypeInfo getSuperType() {
		return isAddress() ? Main.generator.wildcardPtrTypeInfo : null;
	}
	
	public boolean isFunction() {
		return false;
	}
	
	public boolean isArray() {
		return false;
	}
	
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return null;
	}
	
	public abstract void collectRawTypes(Set<RawType> rawTypes);
	
	public int indexToOffsetShallow(ASTNode<?, ?> node, int index) {
		throw Helpers.nodeError(node, "Type \"%s\" can not be indexed!", this);
	}
	
	public int offsetToIndexShallow(ASTNode<?, ?> node, int offset) {
		throw Helpers.nodeError(node, "Type \"%s\" can not be indexed!", this);
	}
	
	public @NonNull TypeInfo atIndex(ASTNode<?, ?> node, int index) {
		throw Helpers.nodeError(node, "Type \"%s\" can not be indexed!", this);
	}
	
	public @NonNull TypeInfo atOffset(ASTNode<?, ?> node, int offset, @NonNull TypeInfo expectedTypeInfo) {
		if (offset == 0 && equals(expectedTypeInfo)) {
			return this;
		}
		else {
			int index = offsetToIndexShallow(node, offset);
			// System.out.println(this + " at " + offset + ", " + index + " for " + expectedTypeInfo);
			return atIndex(node, index).atOffset(node, offset - indexToOffsetShallow(node, index), expectedTypeInfo);
		}
	}
	
	@Override
	public abstract int hashCode();
	
	public abstract boolean equalsOther(Object obj, boolean ignoreReferenceLevels);
	
	@Override
	public boolean equals(Object obj) {
		return equalsOther(obj, false);
	}
	
	public abstract String rawString();
	
	@Override
	public String toString() {
		return Helpers.charLine(Global.ADDRESS_OF, referenceLevel) + rawString();
	}
}
