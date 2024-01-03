package drlc.intermediate.component.type;

import java.util.*;
import java.util.function.Function;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class CompoundTypeInfo extends TypeInfo {
	
	public final List<TypeInfo> typeInfos;
	public final int count;
	
	protected CompoundTypeInfo(ASTNode<?, ?> node, int referenceLevel, List<TypeInfo> typeInfos) {
		super(node, referenceLevel);
		this.typeInfos = typeInfos;
		count = typeInfos.size();
	}
	
	@Override
	public boolean exists(Scope scope) {
		return typeInfos.stream().allMatch(x -> equalsOther(x, true) || x.exists(scope));
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : typeInfos.stream().mapToInt(TypeInfo::getSize).sum();
	}
	
	@Override
	public void collectRawTypes(Set<RawType> rawTypes) {
		if (!isAddress()) {
			for (TypeInfo typeInfo : typeInfos) {
				typeInfo.collectRawTypes(rawTypes);
			}
		}
	}
	
	@Override
	public int indexToOffsetShallow(ASTNode<?, ?> node, int index) {
		if (index >= count) {
			throw Helpers.nodeError(node, "Attempted to index type \"%s\" at position %d!", this, index);
		}
		else {
			int offset = 0;
			for (int i = 0; i < index; ++i) {
				offset += typeInfos.get(i).getSize();
			}
			return offset;
		}
	}
	
	@Override
	public int offsetToIndexShallow(ASTNode<?, ?> node, int offset) {
		int index = 0;
		while ((offset -= typeInfos.get(index).getSize()) >= 0) {
			++index;
		}
		if (index >= count) {
			throw Helpers.nodeError(node, "Attempted to index type \"%s\" at position %d!", this, index);
		}
		else {
			return index;
		}
	}
	
	@Override
	public @NonNull TypeInfo atIndex(ASTNode<?, ?> node, int index) {
		return typeInfos.get(index).modifiedReferenceLevel(node, referenceLevel);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof CompoundTypeInfo) {
			CompoundTypeInfo other = (CompoundTypeInfo) obj;
			boolean equalReferenceLevels = ignoreReferenceLevels || referenceLevel == other.referenceLevel;
			return equalReferenceLevels && typeInfos.equals(other.typeInfos);
		}
		else {
			return false;
		}
	}
	
	protected List<?> nonRecursiveTypeInfos(Function<TypeInfo, ?> fallback) {
		return Helpers.map(typeInfos, x -> equalsOther(x, true) ? fallback.apply(x) : x);
	}
}
