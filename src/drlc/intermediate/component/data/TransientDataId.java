package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
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
	public TransientDataId addAddressPrefix(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to add address prefix to data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId removeAddressPrefix(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to remove address prefix from data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId addDereference(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to add dereference to data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId removeDereference(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to remove dereference from data ID \"%s\"!", this);
	}
	
	@Override
	public TransientDataId removeAllDereferences(ASTNode<?, ?> node) {
		return new TransientDataId(typeInfo);
	}
	
	@Override
	public boolean isCompressable() {
		return false;
	}
	
	@Override
	public boolean isRepeatable(boolean lvalue) {
		return false;
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
