package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.scope.Scope;

public class FunctionItemTypeInfo extends FunctionTypeInfo {
	
	public final @NonNull Function function;
	
	@SuppressWarnings("null")
	public @NonNull FunctionPointerTypeInfo functionPointerTypeInfo = null;
	
	protected FunctionItemTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos, @NonNull Function function) {
		super(node, referenceMutability, returnTypeInfo, paramTypeInfos);
		this.function = function;
		setFunctionPointerTypeInfo();
	}
	
	protected FunctionItemTypeInfo(ASTNode<?> node, @NonNull Function function) {
		this(node, new ArrayList<>(), function.returnTypeInfo, function.paramTypeInfos, function);
	}
	
	public FunctionItemTypeInfo(ASTNode<?> node, Scope scope, String functionName) {
		this(node, scope.getFunction(node, functionName));
	}
	
	@Override
	public List<TypeInfo> getArgTypeInfos() {
		return function.paramTypeInfos.subList(0, function.paramTypeInfos.size() - function.captureTypeInfos.size());
	}
	
	@Override
	public void updateReturnType(@NonNull TypeInfo returnType) {
		super.updateReturnType(returnType);
		setFunctionPointerTypeInfo();
	}
	
	public void setFunctionPointerTypeInfo() {
		functionPointerTypeInfo = new FunctionPointerTypeInfo(null, referenceMutability, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?> node, List<Boolean> referenceMutability) {
		return new FunctionItemTypeInfo(node, referenceMutability, returnTypeInfo, paramTypeInfos, function);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (super.canImplicitCastTo(otherInfo)) {
			return !(otherInfo instanceof FunctionItemTypeInfo) || function.equals(((FunctionItemTypeInfo) otherInfo).function);
		}
		else {
			return functionPointerTypeInfo.canImplicitCastTo(otherInfo);
		}
	}
	
	@Override
	public @Nullable TypeInfo getSuperType() {
		return functionPointerTypeInfo;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, returnTypeInfo, paramTypeInfos, function);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof FunctionItemTypeInfo) {
			FunctionItemTypeInfo other = (FunctionItemTypeInfo) obj;
			return super.equalsOther(obj, ignoreReferenceMutability) && function.equals(other.function);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return super.rawString() + " " + Global.BRACE_START + function.name + Global.BRACE_END;
	}
	
	@Override
	public String routineString() {
		return super.routineString() + " " + Global.BRACE_START + function.name + Global.BRACE_END;
	}
}
