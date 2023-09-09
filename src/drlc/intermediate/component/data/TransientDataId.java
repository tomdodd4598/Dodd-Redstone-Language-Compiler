package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.component.type.TypeInfo;

public class TransientDataId extends DataId {
	
	public TransientDataId(@NonNull TypeInfo typeInfo) {
		super(null, 0, typeInfo);
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return 0;
	}
	
	@Override
	public TransientDataId addAddressPrefix() {
		throw Helpers.nodeError(null, "Attempted to add address prefix to data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId removeAddressPrefix() {
		throw Helpers.nodeError(null, "Attempted to remove address prefix from data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId addDereference() {
		throw Helpers.nodeError(null, "Attempted to add dereference to data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId removeDereference() {
		throw Helpers.nodeError(null, "Attempted to remove dereference from data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId removeAllDereferences() {
		return new TransientDataId(typeInfo);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(scope, dereferenceLevel, typeInfo);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreDereferenceLevels) {
		if (obj instanceof TransientDataId) {
			TransientDataId other = (TransientDataId) obj;
			boolean equalDereferenceLevels = ignoreDereferenceLevels || dereferenceLevel == other.dereferenceLevel;
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && typeInfo.equalsOther(other.typeInfo, ignoreDereferenceLevels);
		}
		else {
			return false;
		}
	}
	
	@Override
	protected String rawString() {
		return Global.TRANSIENT;
	}
}
