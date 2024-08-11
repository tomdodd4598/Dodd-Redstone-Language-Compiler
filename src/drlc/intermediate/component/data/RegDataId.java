package drlc.intermediate.component.data;

import java.util.Objects;

import org.eclipse.jdt.annotation.*;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;

public class RegDataId extends DataId {
	
	public final long regId;
	
	public RegDataId(int dereferenceLevel, @NonNull TypeInfo typeInfo, long regId) {
		super(null, dereferenceLevel, typeInfo);
		this.regId = regId;
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return -1;
	}
	
	public RegDataId replaceId(long regId) {
		return new RegDataId(dereferenceLevel, typeInfo, regId);
	}
	
	@Override
	public @NonNull RegDataId addDereference(ASTNode<?> node) {
		return new RegDataId(dereferenceLevel + 1, typeInfo.modifyMutable(node, -1), regId);
	}
	
	@Override
	public @NonNull RegDataId removeDereference(ASTNode<?> node) {
		return new RegDataId(dereferenceLevel - 1, typeInfo.modifyMutable(node, 1), regId);
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
		return dereferenceLevel <= 0;
	}
	
	@Override
	public boolean isRepeatable(boolean lvalue) {
		return !lvalue || dereferenceLevel > 0;
	}
	
	@Override
	public int hashCode(boolean raw) {
		return Objects.hash(Global.REG, scope, raw ? 0 : dereferenceLevel, raw ? null : typeInfo);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean raw, boolean low) {
		if (obj instanceof RegDataId) {
			RegDataId other = (RegDataId) obj;
			boolean equalDereferenceLevels = raw || low || dereferenceLevel == other.dereferenceLevel;
			boolean equalTypeInfos = low || typeInfo.equalsOther(other.typeInfo, raw);
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && equalTypeInfos && regId == other.regId;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected String rawString() {
		return Global.REG + regId;
	}
}
