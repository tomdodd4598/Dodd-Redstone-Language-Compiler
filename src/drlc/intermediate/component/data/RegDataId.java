package drlc.intermediate.component.data;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;

public class RegDataId extends DataId {
	
	public final long regId;
	public final int offset;
	
	protected final List<RegDataId> offsetFrom;
	
	public RegDataId(int dereferenceLevel, @NonNull TypeInfo typeInfo, long regId) {
		this(dereferenceLevel, typeInfo, regId, 0, new ArrayList<>());
	}
	
	protected RegDataId(int dereferenceLevel, @NonNull TypeInfo typeInfo, long regId, int offset, List<RegDataId> offsetFrom) {
		super(null, dereferenceLevel, typeInfo);
		this.regId = regId;
		this.offset = offset;
		this.offsetFrom = offsetFrom;
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return -1;
	}
	
	public RegDataId replaceId(long regId) {
		return new RegDataId(dereferenceLevel, typeInfo, regId, offset, offsetFrom);
	}
	
	@Override
	public @NonNull RegDataId addDereference(ASTNode<?> node) {
		return new RegDataId(dereferenceLevel + 1, typeInfo.modifyMutable(node, -1), regId, offset, offsetFrom);
	}
	
	@Override
	public @NonNull RegDataId removeDereference(ASTNode<?> node) {
		return new RegDataId(-1, typeInfo.modifyMutable(node, 1), regId, offset, offsetFrom);
	}
	
	protected List<RegDataId> nextIndexFrom() {
		List<RegDataId> nextIndexFrom = new ArrayList<>();
		nextIndexFrom.addAll(offsetFrom);
		nextIndexFrom.add(this);
		return nextIndexFrom;
	}
	
	@Override
	public boolean isIndexed() {
		return !offsetFrom.isEmpty();
	}
	
	@Override
	public @NonNull RegDataId atOffset(ASTNode<?> node, int offset, @NonNull TypeInfo expectedTypeInfo) {
		@NonNull TypeInfo offsetType = typeInfo.atOffset(node, offset, expectedTypeInfo);
		return new RegDataId(dereferenceLevel, offsetType, regId, this.offset + offset, nextIndexFrom());
	}
	
	@Override
	public @Nullable DataId getRawReplacer(ASTNode<?> node, DataId rawInternal) {
		if (rawInternal instanceof RegDataId) {
			RegDataId raw = (RegDataId) rawInternal;
			return new RegDataId(raw.dereferenceLevel, raw.typeInfo.atOffset(node, offset, typeInfo.copyMutable(node, raw.dereferenceLevel)), raw.regId, offset, new ArrayList<>());
		}
		else if (rawInternal instanceof VariableDataId) {
			VariableDataId raw = (VariableDataId) rawInternal;
			return new VariableDataId(raw.dereferenceLevel, raw.variable.atOffset(node, offset, typeInfo.modifyMutable(node, raw.dereferenceLevel)), offset, new ArrayList<>());
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
		return Objects.hash(Global.REG, scope, raw ? 0 : dereferenceLevel, raw ? null : typeInfo, regId, raw ? 0 : offset);
	}
	
	protected boolean matchTypeInfos(RegDataId other, boolean raw) {
		if (raw) {
			return typeInfo.equalsOther(other.typeInfo, true) || offsetFrom.stream().anyMatch(x -> x.typeInfo.equalsOther(other.typeInfo, true));
		}
		else {
			return typeInfo.equalsOther(other.typeInfo, false);
		}
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean raw) {
		if (obj instanceof RegDataId) {
			RegDataId other = (RegDataId) obj;
			boolean equalDereferenceLevels = raw || dereferenceLevel == other.dereferenceLevel;
			boolean equalTypeInfos = matchTypeInfos(other, raw);
			boolean equalOffsets = raw || offset == other.offset;
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && equalTypeInfos && regId == other.regId && equalOffsets;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected String rawString() {
		if (offset == 0) {
			return Global.REG + regId;
		}
		else {
			return Global.REG + regId + Global.FULL_STOP + offset;
		}
	}
}
