package drlc.intermediate.component.data;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
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
			throw Helpers.nodeError(null, "Dereference level for data ID \"%s\" can not be less than %d but was equal to %d!", this, minimumDereferenceLevel, dereferenceLevel);
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
	
	public abstract DataId addAddressPrefix();
	
	public abstract DataId removeAddressPrefix();
	
	public abstract DataId addDereference();
	
	public abstract DataId removeDereference();
	
	public abstract DataId removeAllDereferences();
	
	public TransientDataId getTransient() {
		if (dereferenceLevel == 0) {
			return new TransientDataId(typeInfo);
		}
		else {
			throw Helpers.nodeError(null, "Attempted to replace data ID \"%s\" with transient data ID!", this);
		}
	}
	
	@Override
	public abstract int hashCode();
	
	public abstract boolean equalsOther(Object obj, boolean ignoreDereferenceLevels);
	
	@Override
	public boolean equals(Object obj) {
		return equalsOther(obj, false);
	}
	
	protected abstract String rawString();
	
	@Override
	public String toString() {
		return (dereferenceLevel >= 0 ? Helpers.addDereferences(rawString(), dereferenceLevel) : Helpers.addAddressPrefix(rawString())) + ": " + typeInfo;
	}
	
	public String fullString() {
		if (scope == null) {
			return toString();
		}
		else {
			return toString() + Global.DOUBLE_COLON + scope.globalId;
		}
	}
	
	public String declarationString() {
		return toString();
	}
}
