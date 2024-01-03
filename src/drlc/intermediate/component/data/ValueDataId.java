package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
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
	public @NonNull ValueDataId addAddressPrefix(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to add address prefix to data ID \"%s\"!", this);
	}
	
	@Override
	public @NonNull ValueDataId addDereference(ASTNode<?, ?> node) {
		throw Helpers.nodeError(node, "Attempted to add dereference to data ID \"%s\"!", this);
	}
	
	@Override
	public boolean isIndexed() {
		return false;
	}
	
	@Override
	public @NonNull ValueDataId atOffset(ASTNode<?, ?> node, int offset, @NonNull TypeInfo expectedTypeInfo) {
		return new ValueDataId(value.atOffset(node, offset, expectedTypeInfo));
	}
	
	@Override
	public @Nullable DataId getRawReplacer(ASTNode<?, ?> node, DataId rawInternal) {
		return null;
	}
	
	@Override
	public boolean isCompressable() {
		return true;
	}
	
	@Override
	public boolean isRepeatable(boolean lvalue) {
		return true;
	}
	
	@Override
	public int hashCode(boolean raw) {
		return Objects.hash(scope, raw ? 0 : dereferenceLevel, value);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean raw) {
		if (obj instanceof ValueDataId) {
			ValueDataId other = (ValueDataId) obj;
			boolean equalDereferenceLevels = raw || dereferenceLevel == other.dereferenceLevel;
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
