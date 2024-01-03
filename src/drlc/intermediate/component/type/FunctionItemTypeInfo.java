package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.scope.Scope;

public class FunctionItemTypeInfo extends FunctionTypeInfo {
	
	public final @NonNull Function function;
	
	public final @NonNull FunctionPointerTypeInfo functionPointerTypeInfo;
	
	protected FunctionItemTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, @NonNull Function function, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceMutability, returnTypeInfo, paramTypeInfos);
		this.function = function;
		functionPointerTypeInfo = new FunctionPointerTypeInfo(null, referenceMutability, returnTypeInfo, paramTypeInfos);
	}
	
	protected FunctionItemTypeInfo(ASTNode<?, ?> node, @NonNull Function function) {
		this(node, new ArrayList<>(), function, function.returnTypeInfo, function.paramTypeInfos);
	}
	
	public FunctionItemTypeInfo(ASTNode<?, ?> node, Scope scope, String functionName) {
		this(node, scope.getFunction(node, functionName));
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, List<Boolean> referenceMutability) {
		return new FunctionItemTypeInfo(node, referenceMutability, function, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (super.equalsOther(otherInfo, true) && canImplicitCastToReferenceMutability(otherInfo)) {
			return !(otherInfo instanceof FunctionItemTypeInfo) || function.equals(((FunctionItemTypeInfo) otherInfo).function);
		}
		else {
			return otherInfo.equals(functionPointerTypeInfo) || (isAddress() && otherInfo.equals(Main.generator.wildcardPtrTypeInfo));
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
		return super.rawString() + " {" + function.name + '}';
	}
	
	@Override
	public String routineString() {
		return super.routineString() + " {" + function.name + '}';
	}
}
