package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.scope.Scope;

public class FunctionItemTypeInfo extends FunctionTypeInfo {
	
	public final @NonNull Function function;
	
	public final @NonNull FunctionPointerTypeInfo functionPointerTypeInfo;
	
	protected FunctionItemTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull Function function, @NonNull TypeInfo returnTypeInfo, List<TypeInfo> paramTypeInfos) {
		super(node, referenceLevel, returnTypeInfo, paramTypeInfos);
		this.function = function;
		
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
		
		functionPointerTypeInfo = new FunctionPointerTypeInfo(null, referenceLevel, returnTypeInfo, paramTypeInfos);
	}
	
	protected FunctionItemTypeInfo(ASTNode<?, ?> node, @NonNull Function function) {
		this(node, 0, function, function.returnTypeInfo, function.paramTypeInfos);
	}
	
	public FunctionItemTypeInfo(ASTNode<?, ?> node, Scope scope, String functionName) {
		this(node, scope.getFunction(node, functionName));
	}
	
	@Override
	public @NonNull TypeInfo copy(ASTNode<?, ?> node, int newReferenceLevel) {
		return new FunctionItemTypeInfo(node, newReferenceLevel, function, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean canImplicitCastTo(TypeInfo otherInfo) {
		if (super.equals(otherInfo)) {
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
		return Objects.hash(referenceLevel, returnTypeInfo, paramTypeInfos, function);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof FunctionItemTypeInfo) {
			FunctionItemTypeInfo other = (FunctionItemTypeInfo) obj;
			return super.equalsOther(obj, ignoreReferenceLevels) && function.equals(other.function);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return super.rawString() + " {" + function.name + '}';
	}
}
