package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;

public class TupleTypeInfo extends CompoundTypeInfo {
	
	protected Map<String, MemberInfo> memberMap = new HashMap<>();
	
	@SuppressWarnings("null")
	public TupleTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos) {
		super(node, referenceMutability, typeInfos);
		
		int offset = 0;
		for (int i = 0; i < count; ++i) {
			@NonNull String name = Integer.toString(i);
			@NonNull TypeInfo typeInfo = typeInfos.get(i);
			memberMap.put(name, new MemberInfo(name, typeInfo, i, offset));
			offset += typeInfo.getSize();
		}
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new TupleTypeInfo(node, referenceMutability, typeInfos);
	}
	
	@Override
	public boolean isTuple() {
		return !isAddress();
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (otherInfo.isAddress() && equals(Main.generator.nullTypeInfo)) {
			return true;
		}
		else if (otherInfo instanceof TupleTypeInfo) {
			return super.canImplicitCastTo(otherInfo);
		}
		else {
			return false;
		}
	}
	
	@Override
	public boolean isMemberAccessValid() {
		return true;
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
	public String rawRoutineString() {
		return Helpers.tupleString(Helpers.map(typeInfos, TypeInfo::routineString));
	}
}
