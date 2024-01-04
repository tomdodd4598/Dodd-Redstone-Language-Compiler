package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

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
	public @NonNull TransientDataId addDereference(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to add dereference to data ID \"%s\"!", this);
	}
	
	@Override
	public @NonNull TransientDataId removeDereference(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to remove dereference from data ID \"%s\"!", this);
	}
	
	@Override
	public boolean isIndexed() {
		return false;
	}
	
	@Override
	public @NonNull TransientDataId atOffset(ASTNode<?, ?> node, int offset, @NonNull TypeInfo expectedTypeInfo) {
		throw Helpers.nodeError(node, "Attempted to index data ID \"%s\"!", this);
	}
	
	@Override
	public @Nullable DataId getRawReplacer(ASTNode<?, ?> node, DataId rawInternal) {
		return null;
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
	public int hashCode(boolean raw) {
		return Objects.hash(scope, raw ? 0 : dereferenceLevel, raw ? null : typeInfo);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean raw) {
		if (obj instanceof TransientDataId) {
			TransientDataId other = (TransientDataId) obj;
			boolean equalDereferenceLevels = raw || dereferenceLevel == other.dereferenceLevel;
			boolean equalTypeInfos = typeInfo.equalsOther(other.typeInfo, raw);
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && equalTypeInfos;
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
