package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Variable;

public class VariableDataId extends DataId {
	
	public final @NonNull Variable variable;
	
	public VariableDataId(int dereferenceLevel, @NonNull Variable variable) {
		super(variable.scope, dereferenceLevel, variable.typeInfo.modifyMutable(null, -dereferenceLevel));
		this.variable = variable;
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return -1;
	}
	
	@Override
	public @NonNull VariableDataId addDereference(ASTNode<?> node) {
		return new VariableDataId(dereferenceLevel + 1, variable);
	}
	
	@Override
	public @NonNull VariableDataId removeDereference(ASTNode<?> node) {
		return new VariableDataId(dereferenceLevel - 1, variable);
	}
	
	@Override
	public @Nullable DataId getRawReplacer(ASTNode<?> node, DataId rawInternal) {
		if (rawInternal instanceof RegDataId) {
			RegDataId raw = (RegDataId) rawInternal;
			return new RegDataId(raw.dereferenceLevel, raw.typeInfo, raw.regId);
		}
		else if (rawInternal instanceof VariableDataId) {
			VariableDataId raw = (VariableDataId) rawInternal;
			return new VariableDataId(raw.dereferenceLevel, raw.variable);
		}
		else {
			return null;
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
	public int hashCode(boolean raw) {
		return Objects.hash(scope, raw ? 0 : dereferenceLevel, raw ? null : typeInfo, variable.hashCode(raw));
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean raw, boolean low) {
		if (obj instanceof VariableDataId) {
			VariableDataId other = (VariableDataId) obj;
			boolean equalDereferenceLevels = raw || low || dereferenceLevel == other.dereferenceLevel;
			boolean equalTypeInfos = low || typeInfo.equalsOther(other.typeInfo, raw);
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && equalTypeInfos && variable.equalsOther(other.variable, raw || low);
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
