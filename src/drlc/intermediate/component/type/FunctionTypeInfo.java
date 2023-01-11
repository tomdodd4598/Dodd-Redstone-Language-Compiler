package drlc.intermediate.component.type;

import java.util.Arrays;

import drlc.*;
import drlc.intermediate.Scope;
import drlc.intermediate.component.Function;
import drlc.node.Node;

public class FunctionTypeInfo extends TypeInfo {
	
	public final Function function;
	public final TypeInfo returnTypeInfo;
	public final TypeInfo[] paramTypeInfos;
	
	protected FunctionTypeInfo(Node node, Type type, int referenceLevel, Function function, TypeInfo returnTypeInfo, TypeInfo[] paramTypeInfos) {
		super(node, type, referenceLevel);
		this.function = function;
		this.returnTypeInfo = returnTypeInfo;
		this.paramTypeInfos = paramTypeInfos;
	}
	
	public FunctionTypeInfo(Node node, Scope scope, String functionName) {
		super(node, scope.getType(node, Global.FN), 0);
		this.function = scope.getFunction(node, functionName);
		this.returnTypeInfo = function.returnTypeInfo;
		this.paramTypeInfos = Helpers.paramTypeInfoArray(function.params);
	}
	
	public FunctionTypeInfo(Node node, Scope scope, int referenceLevel, TypeInfo returnTypeInfo, TypeInfo[] paramTypeInfos) {
		this(node, scope.getType(node, Global.FN), referenceLevel, null, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new FunctionTypeInfo(node, type, newReferenceLevel, function, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean canImplicitCastTo(Node node, Generator generator, TypeInfo otherInfo) {
		return isAddress(node) ? super.canImplicitCastTo(node, generator, otherInfo) : equals(otherInfo);
	}
	
	@Override
	public boolean isFunction() {
		return true;
	}
	
	@Override
	public boolean isAddressable() {
		return function == null;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionTypeInfo) {
			FunctionTypeInfo other = (FunctionTypeInfo) obj;
			return type.equals(other.type) && referenceLevel == other.referenceLevel && returnTypeInfo.equals(other.returnTypeInfo) && Arrays.equals(paramTypeInfos, other.paramTypeInfos);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String typeString() {
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		int l = paramTypeInfos.length;
		if (l > 0) {
			for (int i = 0; i < l - 1; ++i) {
				builder.append(paramTypeInfos[i].toString()).append(", ");
			}
			builder.append(paramTypeInfos[l - 1].toString());
		}
		return builder.append(')').append(returnTypeInfo.toString()).toString();
	}
}
