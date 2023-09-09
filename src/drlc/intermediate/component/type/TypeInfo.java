package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class TypeInfo {
	
	public final int referenceLevel;
	
	protected TypeInfo(ASTNode node, int referenceLevel) {
		if (referenceLevel < 0) {
			throw node.error("Reference level of type \"%s\" can not be negative!", rawString());
		}
		this.referenceLevel = referenceLevel;
	}
	
	public abstract @NonNull TypeInfo copy(ASTNode node, int newReferenceLevel);
	
	public @NonNull TypeInfo modifiedReferenceLevel(ASTNode node, int referenceLevelDiff) {
		return copy(node, referenceLevel + referenceLevelDiff);
	}
	
	public abstract boolean exists(Scope scope);
	
	public boolean isAddress() {
		return referenceLevel > 0;
	}
	
	public boolean isVoid() {
		return false;
	}
	
	public boolean isWord() {
		return false;
	}
	
	public abstract int getSize();
	
	public int getAddressOffsetSize(ASTNode node) {
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
