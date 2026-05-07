package drlc.intermediate.component;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.FunctionItemValue;
import drlc.intermediate.scope.Scope;

public class Function {
	
	public final @NonNull String name;
	
	public final boolean builtIn;
	
	public @NonNull TypeInfo returnTypeInfo;
	public final List<DeclaratorInfo> params;
	public final List<Variable> captures = new ArrayList<>();
	
	public final List<TypeInfo> paramTypeInfos;
	public final List<TypeInfo> captureTypeInfos = new ArrayList<>();
	
	public final boolean closure;
	
	public boolean inferReturnType = false;
	protected boolean hasInferredReturnType = false;
	
	public boolean defined = false;
	
	protected Boolean required = null;
	
	public Scope definitionScope = null;
	public Scope functionScope = null;
	
	@SuppressWarnings("null")
	public @NonNull FunctionItemValue value = null;
	
	public Function(ASTNode<?> node, @NonNull String name, boolean builtIn, @NonNull TypeInfo returnTypeInfo, List<DeclaratorInfo> params, boolean closure, boolean defined) {
		this.name = name;
		this.builtIn = builtIn;
		this.returnTypeInfo = returnTypeInfo;
		this.params = params;
		paramTypeInfos = Helpers.map(params, DeclaratorInfo::getTypeInfo);
		this.closure = closure;
		this.defined = defined;
	}
	
	public void setRequired() {
		required = true;
	}
	
	public void setUnused() {
		required = false;
	}
	
	public boolean isExplicitlyRequired() {
		return Boolean.TRUE.equals(required);
	}
	
	public void addCapture(Variable variable, DeclaratorInfo copy) {
		params.add(copy);
		captures.add(variable);
		paramTypeInfos.add(variable.typeInfo);
		captureTypeInfos.add(variable.typeInfo);
	}
	
	public @Nullable DeclaratorInfo getCapturedParam(Variable variable) {
		int index = captures.indexOf(variable);
		return index < 0 ? null : params.get(params.size() - captures.size() + index);
	}
	
	public void updateReturnType(ASTNode<?> node, @NonNull TypeInfo returnType) {
		@NonNull TypeInfo updatedReturnType = returnType;
		if (inferReturnType) {
			if (!hasInferredReturnType) {
				hasInferredReturnType = true;
			}
			else {
				@Nullable TypeInfo mergedReturnType = Helpers.getImplicitCastJoin(returnTypeInfo, returnType);
				if (mergedReturnType == null) {
					throw Helpers.nodeError(node, "Inferred return types \"%s\" and \"%s\" are incompatible for function \"%s\"!", returnTypeInfo, returnType, name);
				}
				updatedReturnType = mergedReturnType;
			}
		}
		returnTypeInfo = updatedReturnType;
		value.typeInfo.updateReturnType(updatedReturnType);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, definitionScope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Function other) {
			return name.equals(other.name) && Objects.equals(definitionScope, other.definitionScope);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Helpers.scopeStringPrefix(definitionScope) + Global.FN + " " + name + Helpers.listString(params) + " " + Global.ARROW + " " + returnTypeInfo;
	}
	
	public String routineString() {
		return Helpers.scopeStringPrefix(definitionScope) + Global.FN + " " + name + Helpers.listString(Helpers.map(params, DeclaratorInfo::routineString)) + " " + Global.ARROW + " " + returnTypeInfo.routineString();
	}
	
	public String asmString() {
		return Helpers.scopeStringPrefix(definitionScope) + name;
	}
}
