package drlc.intermediate.component.type;

import java.util.*;
import java.util.stream.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;

public abstract class TypeInfo {
	
	public final List<Boolean> referenceMutability;
	
	protected TypeInfo(ASTNode<?> node, List<Boolean> referenceMutability) {
		this.referenceMutability = referenceMutability;
	}
	
	public abstract @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability);
	
	public @NonNull TypeInfo copy(ASTNode<?> node, Boolean... referenceMutability) {
		return copy(node, Arrays.asList(referenceMutability));
	}
	
	public @NonNull TypeInfo addressOf(ASTNode<?> node, List<Boolean> referenceMutabilityDiff) {
		List<Boolean> referenceMutabilityCopy = new ArrayList<>(referenceMutability);
		referenceMutabilityCopy.addAll(referenceMutabilityDiff);
		return copy(node, referenceMutabilityCopy);
	}
	
	public @NonNull TypeInfo addressOf(ASTNode<?> node, Boolean... referenceMutabilityDiff) {
		return addressOf(node, Arrays.asList(referenceMutabilityDiff));
	}
	
	public @NonNull TypeInfo dereference(ASTNode<?> node, int dereferenceLevel) {
		int referenceLevel = getReferenceLevel() - dereferenceLevel;
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
		List<Boolean> referenceMutabilityCopy = new ArrayList<>();
		for (int i = 0; i < referenceLevel; ++i) {
			referenceMutabilityCopy.add(referenceMutability.get(i));
		}
		return copy(node, referenceMutabilityCopy);
	}
	
	public @NonNull TypeInfo copyMutable(ASTNode<?> node, int referenceLevel) {
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
		return copy(node, Collections.nCopies(referenceLevel, true));
	}
	
	public @NonNull TypeInfo modifyMutable(ASTNode<?> node, int referenceLevelDiff) {
		return modifyReferenceLevel(node, referenceLevelDiff, true);
	}
	
	public @NonNull TypeInfo modifyReferenceLevel(ASTNode<?> node, int referenceLevelDiff, boolean mutability) {
		return referenceLevelDiff >= 0 ? addressOf(node, Collections.nCopies(referenceLevelDiff, mutability)) : dereference(node, -referenceLevelDiff);
	}
	
	public int getReferenceLevel() {
		return referenceMutability.size();
	}
	
	public boolean isAddress() {
		return getReferenceLevel() > 0;
	}
	
	public boolean isMutableReference() {
		int referenceLevel = getReferenceLevel();
		return referenceLevel > 0 && referenceMutability.get(referenceLevel - 1);
	}
	
	public boolean isWord() {
		return false;
	}
	
	public boolean isArray() {
		return false;
	}
	
	public boolean isTuple() {
		return false;
	}
	
	public abstract int getSize();
	
	public int getAddressOffsetSize(ASTNode<?> node) {
		return dereference(node, 1).getSize();
	}
	
	public @NonNull TypeInfo getImmediateCastType() {
		return this;
	}
	
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		return equalsOther(otherInfo, true) && canImplicitCastToReferenceMutability(otherInfo);
	}
	
	public boolean canImplicitCastToReferenceMutability(TypeInfo otherInfo) {
		int referenceLevel = getReferenceLevel();
		if (referenceLevel != otherInfo.getReferenceLevel()) {
			return false;
		}
		else {
			List<Boolean> otherReferenceMutability = otherInfo.referenceMutability;
			for (int i = 0; i < referenceLevel; ++i) {
				if (!referenceMutability.get(i) && otherReferenceMutability.get(i)) {
					return false;
				}
			}
			return true;
		}
	}
	
	public boolean isMemberAccessValid() {
		return false;
	}
	
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return null;
	}
	
	public abstract void collectTypeDefs(Set<TypeDef> typeDefs);
	
	public int indexToOffsetShallow(ASTNode<?> node, int index) {
		throw Helpers.nodeError(node, "Type \"%s\" can not be indexed!", this);
	}
	
	public int offsetToIndexShallow(ASTNode<?> node, int offset) {
		throw Helpers.nodeError(node, "Type \"%s\" can not be indexed!", this);
	}
	
	public @NonNull TypeInfo atIndex(ASTNode<?> node, int index) {
		throw Helpers.nodeError(node, "Type \"%s\" can not be indexed!", this);
	}
	
	public @NonNull TypeInfo atOffset(ASTNode<?> node, int offset, @NonNull TypeInfo expectedTypeInfo) {
		System.out.println(this + " at offset " + offset + " for " + expectedTypeInfo);
		if (offset == 0 && equalsOther(expectedTypeInfo, true) && getReferenceLevel() == expectedTypeInfo.getReferenceLevel()) {
			System.out.println(this + " matches " + expectedTypeInfo + "\n");
			return this;
		}
		else {
			int index = offsetToIndexShallow(node, offset);
			System.out.println(this + " at index " + index + " for " + expectedTypeInfo);
			return atIndex(node, index).atOffset(node, offset - indexToOffsetShallow(node, index), expectedTypeInfo);
		}
	}
	
	@Override
	public abstract int hashCode();
	
	public abstract boolean equalsOther(Object obj, boolean ignoreReferenceMutability);
	
	@Override
	public boolean equals(Object obj) {
		return equalsOther(obj, false);
	}
	
	public abstract String rawString();
	
	public String getReferenceMutabilityString() {
		int referenceLevel = getReferenceLevel();
		return IntStream.range(0, referenceLevel).mapToObj(x -> referenceMutability.get(referenceLevel - x - 1) ? Global.ADDRESS_OF + Global.MUT + " " : Global.ADDRESS_OF).collect(Collectors.joining());
	}
	
	@Override
	public String toString() {
		return getReferenceMutabilityString() + rawString();
	}
	
	public String rawRoutineString() {
		return rawString();
	}
	
	public String getRoutineReferenceString() {
		return String.join("", Collections.nCopies(getReferenceLevel(), Global.ADDRESS_OF));
	}
	
	public String routineString() {
		return getRoutineReferenceString() + rawRoutineString();
	}
}
