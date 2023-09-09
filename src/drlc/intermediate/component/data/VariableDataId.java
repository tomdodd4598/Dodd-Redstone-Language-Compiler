package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.component.Variable;

public class VariableDataId extends DataId {
	
	public final @NonNull Variable variable;
	
	public VariableDataId(int dereferenceLevel, @NonNull Variable variable) {
		super(variable.scope, dereferenceLevel, variable.typeInfo.modifiedReferenceLevel(null, -dereferenceLevel));
		this.variable = variable;
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return -1;
	}
	
	@Override
	public VariableDataId addAddressPrefix() {
		if (dereferenceLevel == 0) {
			return new VariableDataId(-1, variable);
		}
		else {
			throw Helpers.nodeError(null, "Attempted to add address prefix to data ID \"%s\"!", this);
		}
	}
	
	@Override
	public VariableDataId removeAddressPrefix() {
		if (isAddress()) {
			return new VariableDataId(dereferenceLevel + 1, variable);
		}
		else {
			throw Helpers.nodeError(null, "Attempted to remove address prefix from data ID \"%s\"!", this);
		}
	}
	
	@Override
	public VariableDataId addDereference() {
		return new VariableDataId(dereferenceLevel + 1, variable);
	}
	
	@Override
	public VariableDataId removeDereference() {
		if (!isDereferenced()) {
			throw Helpers.nodeError(null, "Attempted to remove dereference from data ID \"%s\"!", this);
		}
		else {
			return new VariableDataId(dereferenceLevel - 1, variable);
		}
	}
	
	@Override
	public VariableDataId removeAllDereferences() {
		if (isAddress()) {
			throw Helpers.nodeError(null, "Attempted to remove all dereferences from data ID \"%s\"!", this);
		}
		else {
			return new VariableDataId(0, variable);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(scope, dereferenceLevel, typeInfo, variable);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreDereferenceLevels) {
		if (obj instanceof VariableDataId) {
			VariableDataId other = (VariableDataId) obj;
			boolean equalDereferenceLevels = ignoreDereferenceLevels || dereferenceLevel == other.dereferenceLevel;
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && typeInfo.equalsOther(other.typeInfo, ignoreDereferenceLevels) && variable.equals(other.variable);
		}
		else {
			return false;
		}
	}
	
	@Override
	protected String rawString() {
		return variable.name;
	}
}
