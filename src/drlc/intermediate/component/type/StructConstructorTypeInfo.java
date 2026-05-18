package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.TypeDef;

public class StructConstructorTypeInfo extends FunctionTypeInfo {
	
	public final @NonNull StructTypeInfo structTypeInfo;
	public final @NonNull TypeDef typeDef;
	
	public StructConstructorTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull StructTypeInfo structTypeInfo) {
		super(node, referenceMutability, structTypeInfo, structTypeInfo.typeInfos);
		this.structTypeInfo = structTypeInfo;
		typeDef = structTypeInfo.typeDef;
	}
	
	public StructConstructorTypeInfo(ASTNode<?> node, @NonNull StructTypeInfo structTypeInfo) {
		this(node, new ArrayList<>(), structTypeInfo);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new StructConstructorTypeInfo(node, referenceMutability, structTypeInfo);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		return equalsOther(otherInfo, true) && canImplicitCastToReferenceMutability(otherInfo);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, structTypeInfo, typeDef);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof StructConstructorTypeInfo other) {
			return super.equalsOther(obj, ignoreReferenceMutability) && typeDef.equals(other.typeDef);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return typeDef + Helpers.listString(paramTypeInfos) + " -> " + structTypeInfo;
	}
	
	@Override
	public String rawRoutineString() {
		return typeDef + Helpers.listString(Helpers.map(paramTypeInfos, TypeInfo::routineString)) + " -> " + structTypeInfo.routineString();
	}
}
