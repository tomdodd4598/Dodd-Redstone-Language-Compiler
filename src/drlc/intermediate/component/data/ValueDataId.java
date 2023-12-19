package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.value.Value;

public class ValueDataId extends DataId {
	
	public final @NonNull Value value;
	
	public ValueDataId(@NonNull Value value) {
		super(null, 0, value.typeInfo);
		this.value = value;
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return 0;
	}
	
	@Override
	public ValueDataId addAddressPrefix(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to add address prefix to data ID \"%s\"!", this);
	}
	
	@Override
	public ValueDataId removeAddressPrefix(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to remove address prefix from data ID \"%s\"!", this);
	}
	
	@Override
	public ValueDataId addDereference(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to add dereference to data ID \"%s\"!", this);
	}
	
	@Override
	public ValueDataId removeDereference(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to remove dereference from data ID \"%s\"!", this);
	}
	
	@Override
	public ValueDataId removeAllDereferences(ASTNode<?, ?> node) {
		return new ValueDataId(value);
	}
	
	@Override
	public boolean isCompressable() {
		return false;
	}
	
	@Override
	public boolean isRepeatable() {
		return true;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(scope, dereferenceLevel, typeInfo, value);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreDereferenceLevels) {
		if (obj instanceof ValueDataId) {
			ValueDataId other = (ValueDataId) obj;
			boolean equalDereferenceLevels = ignoreDereferenceLevels || dereferenceLevel == other.dereferenceLevel;
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && value.equals(other.value);
		}
		else {
			return false;
		}
	}
	
	@Override
	protected String rawString() {
		return Global.IMMEDIATE + value.valueString();
	}
}
