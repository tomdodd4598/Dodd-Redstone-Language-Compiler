package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;

public class TupleTypeInfo extends CompoundTypeInfo {
	
	protected Map<String, MemberInfo> memberMap = new HashMap<>();
	
	@SuppressWarnings("null")
	public TupleTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos) {
		super(node, referenceMutability, typeInfos);
		
		if (referenceMutability.isEmpty()) {
			int offset = 0;
			for (int i = 0; i < count; ++i) {
				@NonNull String name = Integer.toString(i);
				@NonNull TypeInfo typeInfo = typeInfos.get(i);
				memberMap.put(name, new MemberInfo(name, typeInfo, i, offset));
				offset += typeInfo.getSize();
			}
		}
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, List<Boolean> referenceMutability) {
		return new TupleTypeInfo(node, referenceMutability, typeInfos);
	}
	
	@Override
	public int getAddressOffsetSize(ASTNode<?, ?> node) {
		return equals(Main.generator.wildcardPtrTypeInfo) ? 1 : super.getAddressOffsetSize(node);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (equalsOther(otherInfo, true) && canImplicitCastToReferenceMutability(otherInfo)) {
			return true;
		}
		else {
			return equals(Main.generator.wildcardPtrTypeInfo) && otherInfo.isAddress();
		}
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return isAddress() && !equals(Main.generator.wildcardPtrTypeInfo) ? Main.generator.wildcardPtrTypeInfo : null;
	}
	
	@Override
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return memberMap.get(memberName);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, nonRecursiveTypeInfos(x -> null));
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof TupleTypeInfo) {
			return super.equalsOther(obj, ignoreReferenceMutability);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Helpers.tupleString(typeInfos);
	}
	
	@Override
	public String routineString() {
		return getRoutineReferenceString() + Helpers.tupleString(Helpers.map(typeInfos, TypeInfo::routineString));
	}
}
