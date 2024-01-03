package drlc.intermediate.component.data;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Variable;
import drlc.intermediate.component.type.TypeInfo;

public class VariableDataId extends DataId {
	
	public final @NonNull Variable variable;
	public final int offset;
	
	protected final List<VariableDataId> offsetFrom;
	
	public VariableDataId(int dereferenceLevel, @NonNull Variable variable) {
		this(dereferenceLevel, variable, 0, new ArrayList<>());
	}
	
	protected VariableDataId(int dereferenceLevel, @NonNull Variable variable, int offset, List<VariableDataId> offsetFrom) {
		super(variable.scope, dereferenceLevel, variable.typeInfo.modifyMutable(null, -dereferenceLevel));
		this.variable = variable;
		this.offset = offset;
		this.offsetFrom = offsetFrom;
	}
	
	@Override
	protected int minimumDereferenceLevel() {
		return -1;
	}
	
	@Override
	public @NonNull VariableDataId addAddressPrefix(ASTNode<?, ?> node) {
		if (dereferenceLevel == 0) {
			return new VariableDataId(-1, variable, offset, offsetFrom);
		}
		else {
			throw Helpers.nodeError(node, "Attempted to add address prefix to data ID \"%s\"!", this);
		}
	}
	
	@Override
	public @NonNull VariableDataId addDereference(ASTNode<?, ?> node) {
		return new VariableDataId(dereferenceLevel + 1, variable, offset, offsetFrom);
	}
	
	protected List<VariableDataId> nextIndexFrom() {
		List<VariableDataId> nextIndexFrom = new ArrayList<>();
		nextIndexFrom.addAll(offsetFrom);
		nextIndexFrom.add(this);
		return nextIndexFrom;
	}
	
	@Override
	public boolean isIndexed() {
		return !offsetFrom.isEmpty();
	}
	
	@Override
	public @NonNull VariableDataId atOffset(ASTNode<?, ?> node, int offset, @NonNull TypeInfo expectedTypeInfo) {
		@NonNull Variable offsetVariable = variable.atOffset(node, offset, expectedTypeInfo);
		return new VariableDataId(dereferenceLevel, offsetVariable, this.offset + offset, nextIndexFrom());
	}
	
	@Override
	public @Nullable DataId getRawReplacer(ASTNode<?, ?> node, DataId rawInternal) {
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
		return dereferenceLevel < 0;
	}
	
	@Override
	public boolean isRepeatable(boolean lvalue) {
		return true;
	}
	
	@Override
	public int hashCode(boolean raw) {
		return Objects.hash(scope, raw ? 0 : dereferenceLevel, raw ? null : typeInfo, variable, raw ? 0 : offset);
	}
	
	protected boolean matchTypeInfos(VariableDataId other, boolean raw) {
		if (raw) {
			return typeInfo.equalsOther(other.typeInfo, true) || offsetFrom.stream().anyMatch(x -> x.typeInfo.equalsOther(other.typeInfo, true));
		}
		else {
			return typeInfo.equalsOther(other.typeInfo, false);
		}
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean raw) {
		if (obj instanceof VariableDataId) {
			VariableDataId other = (VariableDataId) obj;
			boolean equalDereferenceLevels = raw || dereferenceLevel == other.dereferenceLevel;
			boolean equalTypeInfos = matchTypeInfos(other, raw);
			boolean equalOffsets = raw || offset == other.offset;
			return Objects.equals(scope, other.scope) && equalDereferenceLevels && equalTypeInfos && variable.equals(other.variable) && equalOffsets;
		}
		else {
			return false;
		}
	}
	
	@Override
	protected String rawString() {
		if (offset == 0) {
			return variable.name;
		}
		else {
			return variable.name + Global.FULL_STOP + offset;
		}
	}
}
