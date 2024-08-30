package drlc.intermediate.component.type;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;

public abstract class CompoundTypeInfo extends TypeInfo {
	
	public final List<TypeInfo> typeInfos;
	public final int count;
	
	protected CompoundTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos) {
		super(node, referenceMutability);
		this.typeInfos = typeInfos;
		count = typeInfos.size();
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : Helpers.sumToInt(typeInfos, TypeInfo::getSize);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (otherInfo instanceof CompoundTypeInfo otherCompoundInfo) {
			if (count == otherCompoundInfo.count && canImplicitCastToReferenceMutability(otherInfo)) {
				return IntStream.range(0, count).allMatch(x -> typeInfos.get(x).canImplicitCastTo(otherCompoundInfo.typeInfos.get(x)));
			}
		}
		return false;
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {
		if (!isAddress()) {
			for (TypeInfo typeInfo : typeInfos) {
				typeInfo.collectTypeDefs(typeDefs);
			}
		}
	}
	
	@Override
	public int indexToOffsetShallow(ASTNode<?> node, int index) {
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
	public int offsetToIndexShallow(ASTNode<?> node, int offset) {
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
	public @NonNull TypeInfo atIndex(ASTNode<?> node, int index) {
		return typeInfos.get(index).addressOf(node, referenceMutability);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof CompoundTypeInfo other) {
			boolean equalReferenceMutability = ignoreReferenceMutability || referenceMutability.equals(other.referenceMutability);
			return equalReferenceMutability && typeInfos.equals(other.typeInfos);
		}
		else {
			return false;
		}
	}
	
	protected List<?> nonRecursiveTypeInfos(Function<TypeInfo, ?> fallback) {
		return Helpers.map(typeInfos, x -> equalsOther(x, true) ? fallback.apply(x) : x);
	}
}
