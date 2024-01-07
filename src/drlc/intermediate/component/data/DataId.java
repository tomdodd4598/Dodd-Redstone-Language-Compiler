package drlc.intermediate.component.data;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public abstract class DataId {
	
	public final Scope scope;
	public final int dereferenceLevel;
	public final @NonNull TypeInfo typeInfo;
	
	protected DataId(Scope scope, int dereferenceLevel, @NonNull TypeInfo typeInfo) {
		this.scope = scope;
		this.dereferenceLevel = dereferenceLevel;
		
		int minimumDereferenceLevel = minimumDereferenceLevel();
		if (dereferenceLevel < minimumDereferenceLevel) {
			throw Helpers.error("Dereference level for data ID \"%s\" can not be less than %d, but was equal to %d!", this, minimumDereferenceLevel, dereferenceLevel);
		}
		
		this.typeInfo = typeInfo;
	}
	
	protected abstract int minimumDereferenceLevel();
	
	public boolean isAddress() {
		return dereferenceLevel < 0;
	}
	
	public boolean isDereferenced() {
		return dereferenceLevel > 0;
	}
	
	protected Long scopeId() {
		return scope == null ? null : scope.globalId;
	}
	
	public abstract @NonNull DataId addDereference(ASTNode<?, ?> node);
	
	public abstract @NonNull DataId removeDereference(ASTNode<?, ?> node);
	
	public @NonNull TransientDataId getTransient(ASTNode<?, ?> node) {
		if (dereferenceLevel == 0) {
			return new TransientDataId(typeInfo);
		}
		else {
			throw Helpers.nodeError(node, "Attempted to replace data ID \"%s\" with transient data ID!", this);
		}
	}
	
	public abstract boolean isIndexed();
	
	public abstract @NonNull DataId atOffset(ASTNode<?, ?> node, int offset, @NonNull TypeInfo expectedTypeInfo);
	
	public abstract @Nullable DataId getRawReplacer(ASTNode<?, ?> node, DataId rawInternal);
	
	public abstract boolean isCompressable();
	
	public abstract boolean isRepeatable(boolean lvalue);
	
	public abstract int hashCode(boolean raw);
	
	@Override
	public int hashCode() {
		return hashCode(false);
	}
	
	public abstract boolean equalsOther(Object obj, boolean raw);
	
	@Override
	public boolean equals(Object obj) {
		return equalsOther(obj, false);
	}
	
	protected abstract String rawString();
	
	@Override
	public String toString() {
		return (dereferenceLevel >= 0 ? Helpers.dereferenceString(dereferenceLevel) : Global.ADDRESS_OF) + Helpers.scopeStringPrefix(scope) + rawString();
	}
}
