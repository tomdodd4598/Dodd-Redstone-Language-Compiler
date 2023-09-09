package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.component.type.TypeInfo;

public class ExtraRegDataId extends DataId {
	
	public final long regId;
	
	public ExtraRegDataId(int dereferenceLevel, @NonNull TypeInfo typeInfo, long regId) {
		super(null, dereferenceLevel, typeInfo);
		this.regId = regId;
	}
	
	public ExtraRegDataId(@NonNull TypeInfo typeInfo, long regId) {
		this(0, typeInfo, regId);
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return -1;
	}
	
	@Override
	public ExtraRegDataId addAddressPrefix() {
		if (dereferenceLevel == 0) {
			return new ExtraRegDataId(-1, typeInfo.modifiedReferenceLevel(null, 1), regId);
		}
		else {
			throw Helpers.nodeError(null, "Attempted to add address prefix to data ID \"%s\"!", this);
		}
	}
	
	@Override
	public ExtraRegDataId removeAddressPrefix() {
		if (isAddress()) {
			return new ExtraRegDataId(dereferenceLevel + 1, typeInfo.modifiedReferenceLevel(null, -1), regId);
		}
		else {
			throw Helpers.nodeError(null, "Attempted to remove address prefix from data ID \"%s\"!", this);
		}
	}
	
	@Override
	public ExtraRegDataId addDereference() {
		return new ExtraRegDataId(dereferenceLevel + 1, typeInfo.modifiedReferenceLevel(null, -1), regId);
	}
	
	@Override
	public ExtraRegDataId removeDereference() {
		if (!isDereferenced()) {
			throw Helpers.nodeError(null, "Attempted to remove dereference from data ID \"%s\"!", this);
		}
		else {
			return new ExtraRegDataId(dereferenceLevel - 1, typeInfo.modifiedReferenceLevel(null, 1), regId);
		}
	}
	
	@Override
	public ExtraRegDataId removeAllDereferences() {
		if (isAddress()) {
			throw Helpers.nodeError(null, "Attempted to remove all dereferences from data ID \"%s\"!", this);
		}
		else {
			return new ExtraRegDataId(0, typeInfo.modifiedReferenceLevel(null, dereferenceLevel), regId);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(Global.EXTRA_REG, scope, dereferenceLevel, typeInfo, regId);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreDereferenceLevels) {
		if (obj instanceof ExtraRegDataId) {
			ExtraRegDataId other = (ExtraRegDataId) obj;
			boolean equalDereferenceLevels = ignoreDereferenceLevels || dereferenceLevel == other.dereferenceLevel;
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && typeInfo.equalsOther(other.typeInfo, ignoreDereferenceLevels) && regId == other.regId;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected String rawString() {
		return Global.EXTRA_REG + regId;
	}
}
