package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;

public class ClosureTypeInfo extends CompoundTypeInfo {
	
	public final @NonNull Function function;
	
	public ClosureTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull Function function) {
		super(node, referenceMutability, function.captureTypeInfos);
		this.function = function;
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new ClosureTypeInfo(node, referenceMutability, function);
	}
	
	public boolean canCoerceToFunctionType(TypeInfo otherInfo) {
		if (!(otherInfo instanceof FunctionTypeInfo functionTypeInfo) || count != 0 || isAddress()) {
			return false;
		}
		
		FunctionItemTypeInfo functionItemTypeInfo = function.value.typeInfo;
		if (functionItemTypeInfo.canImplicitCastTo(otherInfo)) {
			return true;
		}
		
		if (function.inferReturnType && !function.defined) {
			FunctionPointerTypeInfo functionPointerTypeInfo = functionItemTypeInfo.functionPointerTypeInfo;
			return functionPointerTypeInfo.getArgTypeInfos().equals(functionTypeInfo.getArgTypeInfos()) && functionPointerTypeInfo.canImplicitCastToReferenceMutability(otherInfo);
		}
		
		return false;
	}
	
	public @Nullable TypeInfo getFunctionTypeCoercion(@Nullable TypeInfo targetType) {
		return targetType != null && canCoerceToFunctionType(targetType) ? targetType : null;
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (otherInfo instanceof ClosureTypeInfo closureTypeInfo) {
			return super.canImplicitCastTo(otherInfo) && function.equals(closureTypeInfo.function);
		}
		else {
			return canCoerceToFunctionType(otherInfo);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, function);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof ClosureTypeInfo other) {
			return super.equalsOther(obj, ignoreReferenceMutability) && function.equals(other.function);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return Global.FN + " " + function.name + Helpers.listString(function.value.typeInfo.getArgTypeInfos()) + " " + Helpers.captureString(typeInfos) + " " + Global.ARROW + " " + function.returnTypeInfo;
	}
	
	@Override
	public String rawRoutineString() {
		return Global.FN + " " + function.name + Helpers.listString(Helpers.map(function.value.typeInfo.getArgTypeInfos(), TypeInfo::routineString)) + " " + Helpers.captureString(Helpers.map(typeInfos, TypeInfo::routineString)) + " " + Global.ARROW + " " + function.returnTypeInfo.routineString();
	}
}
