package drlc.intermediate.component.type;

import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.TypeDef;

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
		if (isAddress()) {
			return Main.generator.getAddressSize();
		}
		
		try {
			int size = 0;
			for (TypeInfo typeInfo : typeInfos) {
				size = Math.addExact(size, typeInfo.getSize());
			}
			return size;
		}
		catch (ArithmeticException e) {
			throw Helpers.error("Size of type \"%s\" is too large!", rawString());
		}
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
		if (index < 0 || index >= count) {
			throw Helpers.nodeError(node, "Attempted to index type \"%s\" at position %d!", this, index);
		}
		else {
			try {
				int offset = 0;
				for (int i = 0; i < index; ++i) {
					offset = Math.addExact(offset, typeInfos.get(i).getSize());
				}
				return offset;
			}
			catch (ArithmeticException e) {
				throw Helpers.nodeError(node, "Offset of type \"%s\" at position %d is too large!", this, index);
			}
		}
	}
	
	@Override
	public int offsetToIndexShallow(ASTNode<?> node, int offset) {
		if (offset < 0) {
			throw Helpers.nodeError(node, "Attempted to index type \"%s\" at position %d!", this, offset);
		}
		
		int remainingOffset = offset;
		for (int index = 0; index < count; ++index) {
			int size = typeInfos.get(index).getSize();
			if (remainingOffset < size) {
				return index;
			}
			remainingOffset -= size;
		}
		
		throw Helpers.nodeError(node, "Attempted to index type \"%s\" at position %d!", this, count);
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
