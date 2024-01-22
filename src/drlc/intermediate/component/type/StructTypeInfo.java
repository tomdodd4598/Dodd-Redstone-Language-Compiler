package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.scope.Scope;

public class StructTypeInfo extends CompoundTypeInfo {
	
	public final @NonNull TypeDef typeDef;
	
	protected StructTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos, @NonNull TypeDef typeDef) {
		super(node, referenceMutability, typeInfos);
		this.typeDef = typeDef;
	}
	
	public StructTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos, Scope scope, @NonNull String typeDefName) {
		this(node, referenceMutability, typeInfos, scope.getTypeDef(node, typeDefName, false));
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new StructTypeInfo(node, referenceMutability, typeInfos, typeDef);
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : typeDef.size;
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		return equalsOther(otherInfo, true) && canImplicitCastToReferenceMutability(otherInfo);
	}
	
	@Override
	public boolean isMemberAccessValid() {
		return true;
	}
	
	@Override
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return typeDef.memberMap.get(memberName);
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {
		if (!isAddress()) {
			typeDefs.add(typeDef);
		}
		super.collectTypeDefs(typeDefs);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, nonRecursiveTypeInfos(x -> null), typeDef);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof StructTypeInfo) {
			return super.equalsOther(obj, ignoreReferenceMutability) && typeDef.equals(((StructTypeInfo) obj).typeDef);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return typeDef.rawString();
	}
	
	@Override
	public String rawRoutineString() {
		return Helpers.structString(typeDef.toString(), nonRecursiveTypeInfos(x -> x.getRoutineReferenceString() + ((StructTypeInfo) x).typeDef));
	}
}
