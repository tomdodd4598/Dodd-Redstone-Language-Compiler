package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
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
	public VariableDataId addAddressPrefix(ASTNode<?, ?> node) {
		if (dereferenceLevel == 0) {
			return new VariableDataId(-1, variable);
		}
		else {
			throw Helpers.nodeError(node, "Attempted to add address prefix to data ID \"%s\"!", this);
		}
	}
	
	@Override
	public VariableDataId removeAddressPrefix(ASTNode<?, ?> node) {
		if (isAddress()) {
			return new VariableDataId(dereferenceLevel + 1, variable);
		}
		else {
			throw Helpers.nodeError(node, "Attempted to remove address prefix from data ID \"%s\"!", this);
		}
	}
	
	@Override
	public VariableDataId addDereference(ASTNode<?, ?> node) {
		return new VariableDataId(dereferenceLevel + 1, variable);
	}
	
	@Override
	public VariableDataId removeDereference(ASTNode<?, ?> node) {
		if (!isDereferenced()) {
			throw Helpers.nodeError(node, "Attempted to remove dereference from data ID \"%s\"!", this);
		}
		else {
			return new VariableDataId(dereferenceLevel - 1, variable);
		}
	}
	
	@Override
	public VariableDataId removeAllDereferences(ASTNode<?, ?> node) {
		if (isAddress()) {
			throw Helpers.nodeError(node, "Attempted to remove all dereferences from data ID \"%s\"!", this);
		}
		else {
			return new VariableDataId(0, variable);
		}
	}
	
	@Override
	public boolean isCompressable() {
		return dereferenceLevel < 0;
	}
	
	@Override
	public boolean isRepeatable(boolean lvalue) {
		return true;
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
