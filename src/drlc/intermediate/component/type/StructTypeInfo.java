package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.scope.Scope;

public class StructTypeInfo extends CompoundTypeInfo {
	
	public final @NonNull TypeDefinition typedef;
	
	public final @NonNull TupleTypeInfo tupleTypeInfo;
	
	protected StructTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos, @NonNull TypeDefinition typedef) {
		super(node, referenceMutability, typeInfos);
		this.typedef = typedef;
		tupleTypeInfo = new TupleTypeInfo(null, referenceMutability, typeInfos);
	}
	
	public StructTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, List<TypeInfo> typeInfos, Scope scope, @NonNull String typedefName) {
		this(node, referenceMutability, typeInfos, scope.getTypedef(node, typedefName));
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new StructTypeInfo(node, referenceMutability, typeInfos, typedef);
	}
	
	@Override
	public boolean exists(Scope scope) {
		return scope.typedefExists(typedef.name, false) && super.exists(scope);
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : typedef.size;
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (super.equalsOther(otherInfo, true) && canImplicitCastToReferenceMutability(otherInfo)) {
			return !(otherInfo instanceof StructTypeInfo) || typedef.equals(((StructTypeInfo) otherInfo).typedef);
		}
		else {
			return tupleTypeInfo.canImplicitCastTo(otherInfo);
		}
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return tupleTypeInfo;
	}
	
	@Override
	public @Nullable MemberInfo getMemberInfo(@NonNull String memberName) {
		return isAddress() ? null : typedef.getMemberInfo(memberName);
	}
	
	@Override
	public void collectTypedefs(Set<TypeDefinition> typedefs) {
		if (!isAddress()) {
			typedefs.add(typedef);
		}
		super.collectTypedefs(typedefs);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, nonRecursiveTypeInfos(x -> null), typedef);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof StructTypeInfo) {
			StructTypeInfo other = (StructTypeInfo) obj;
			return super.equalsOther(obj, ignoreReferenceMutability) && typedef.equals(other.typedef);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Helpers.structString(typedef, nonRecursiveTypeInfos(x -> x.getReferenceMutabilityString() + ((StructTypeInfo) x).typedef));
	}
	
	@Override
	public String routineString() {
		return getRoutineReferenceString() + Helpers.structString(typedef, nonRecursiveTypeInfos(x -> x.getRoutineReferenceString() + ((StructTypeInfo) x).typedef));
	}
}
