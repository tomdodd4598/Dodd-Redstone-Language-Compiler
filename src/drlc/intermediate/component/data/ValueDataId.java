package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.value.*;

public class ValueDataId extends DataId {
	
	public final @NonNull Value<?> value;
	
	public ValueDataId(@NonNull Value<?> value) {
		super(null, 0, value.typeInfo);
		this.value = value;
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return 0;
	}
	
	@Override
	public @Nullable Function getFunction() {
		return value instanceof FunctionItemValue ? ((FunctionItemValue) value).typeInfo.function : null;
	}
	
	@Override
	public @NonNull ValueDataId addDereference(ASTNode<?> node) {
		throw Helpers.nodeError(node, "Attempted to add dereference to data ID \"%s\"!", this);
	}
	
	@Override
	public @NonNull ValueDataId removeDereference(ASTNode<?> node) {
		throw Helpers.nodeError(node, "Attempted to remove dereference from data ID \"%s\"!", this);
	}
	
	@Override
	public @Nullable DataId getRawReplacer(ASTNode<?> node, DataId rawInternal) {
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
	public boolean equalsOther(Object obj, boolean raw, boolean low) {
		if (obj instanceof ValueDataId) {
			ValueDataId other = (ValueDataId) obj;
			boolean equalDereferenceLevels = raw || low || dereferenceLevel == other.dereferenceLevel;
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
