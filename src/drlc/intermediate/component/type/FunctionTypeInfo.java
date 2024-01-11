package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class FunctionTypeInfo extends TypeInfo {
	
	public @NonNull TypeInfo returnTypeInfo;
	protected final List<TypeInfo> paramTypeInfos;
	
	protected FunctionTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceMutability);
		this.returnTypeInfo = returnTypeInfo;
		this.paramTypeInfos = paramTypeInfos;
	}
	
	public List<TypeInfo> getArgTypeInfos() {
		return paramTypeInfos;
	}
	
	public void updateReturnType(@NonNull TypeInfo returnType) {
		returnTypeInfo = returnType;
	}
	
	@Override
	public boolean exists(Scope scope) {
		return returnTypeInfo.exists(scope) && paramTypeInfos.stream().allMatch(x -> x.exists(scope));
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : Main.generator.getFunctionSize();
	}
	
	@Override
	public @Nullable FunctionTypeInfo getFunction() {
		return isAddress() ? null : this;
	}
	
	@Override
	public void collectTypedefs(Set<TypeDefinition> typedefs) {}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof FunctionTypeInfo) {
			FunctionTypeInfo other = (FunctionTypeInfo) obj;
			boolean equalReferenceMutability = ignoreReferenceMutability || referenceMutability.equals(other.referenceMutability);
			return equalReferenceMutability && returnTypeInfo.equals(other.returnTypeInfo) && paramTypeInfos.equals(other.paramTypeInfos);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Global.FN + Helpers.listString(paramTypeInfos) + " " + Global.ARROW + " " + returnTypeInfo;
	}
	
	@Override
	public String routineString() {
		return getRoutineReferenceString() + Global.FN + Helpers.listString(Helpers.map(paramTypeInfos, TypeInfo::routineString)) + " " + Global.ARROW + " " + returnTypeInfo.routineString();
	}
}
