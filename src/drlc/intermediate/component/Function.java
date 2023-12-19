package drlc.intermediate.component;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;

public class Function {
	
	public final @NonNull String name;
	
	public final boolean builtIn;
	
	public final @NonNull TypeInfo returnTypeInfo;
	public final List<DeclaratorInfo> params;
	
	public final List<TypeInfo> paramTypeInfos;
	
	public boolean required;
	
	public Function(ASTNode<?, ?> node, @NonNull String name, boolean builtIn, @NonNull TypeInfo returnTypeInfo, List<DeclaratorInfo> params) {
		this.name = name;
		this.builtIn = builtIn;
		this.returnTypeInfo = returnTypeInfo;
		this.params = params;
		paramTypeInfos = Helpers.paramTypeInfos(params);
		required = false;
	}
	
	public int getArgumentCount() {
		return params.size();
	}
	
	public boolean typeEquals(FunctionTypeInfo functionTypeInfo) {
		return returnTypeInfo.equals(functionTypeInfo.returnTypeInfo) && paramTypeInfos.equals(functionTypeInfo.paramTypeInfos);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Function) {
			Function other = (Function) obj;
			return name.equals(other.name) && returnTypeInfo.equals(other.returnTypeInfo) && paramTypeInfos.equals(other.paramTypeInfos);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Global.FN + ' ' + name + Helpers.listString(params) + " -> " + returnTypeInfo;
	}
}
