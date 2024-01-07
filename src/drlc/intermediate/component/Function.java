package drlc.intermediate.component;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public class Function {
	
	public final @NonNull String name;
	
	public final boolean builtIn;
	
	public final @NonNull TypeInfo returnTypeInfo;
	public final List<DeclaratorInfo> params;
	
	public final List<TypeInfo> paramTypeInfos;
	
	protected boolean localRequired = false;
	protected Boolean globalRequired = null;
	
	public Scope scope;
	
	public boolean defined = false;
	
	public Function(ASTNode<?, ?> node, @NonNull String name, boolean builtIn, @NonNull TypeInfo returnTypeInfo, List<DeclaratorInfo> params, boolean defined) {
		this.name = name;
		this.builtIn = builtIn;
		this.returnTypeInfo = returnTypeInfo;
		this.params = params;
		paramTypeInfos = Helpers.map(params, DeclaratorInfo::getTypeInfo);
		this.defined = defined;
	}
	
	public void setUnused() {
		globalRequired = false;
	}
	
	public void setRequired(boolean global) {
		if (global) {
			globalRequired = true;
		}
		else {
			localRequired = true;
		}
	}
	
	public boolean isRequired() {
		if (globalRequired != null) {
			return globalRequired;
		}
		else if (localRequired) {
			Function outerFunction = scope.getContextFunction();
			return outerFunction == null || outerFunction.isRequired();
		}
		else {
			return false;
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, scope, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Function) {
			Function other = (Function) obj;
			return name.equals(other.name) && Objects.equals(scope, other.scope) && returnTypeInfo.equals(other.returnTypeInfo) && paramTypeInfos.equals(other.paramTypeInfos);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Helpers.scopeStringPrefix(scope) + Global.FN + " " + name + Helpers.listString(params) + " " + Global.ARROW + " " + returnTypeInfo;
	}
	
	public String routineString() {
		return Helpers.scopeStringPrefix(scope) + Global.FN + " " + name + Helpers.listString(Helpers.map(params, DeclaratorInfo::routineString)) + " " + Global.ARROW + " " + returnTypeInfo.routineString();
	}
}
