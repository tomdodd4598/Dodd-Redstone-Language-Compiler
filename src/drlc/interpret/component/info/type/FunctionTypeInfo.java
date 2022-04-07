package drlc.interpret.component.info.type;

import java.util.Arrays;

import drlc.*;
import drlc.generate.Generator;
import drlc.interpret.Scope;
import drlc.interpret.component.*;
import drlc.node.Node;

public class FunctionTypeInfo extends TypeInfo {
	
	public final Function function;
	public final TypeInfo returnTypeInfo;
	public final TypeInfo[] paramTypeInfos;
	
	private FunctionTypeInfo(Node node, int referenceLevel, Function function, TypeInfo returnTypeInfo, TypeInfo[] paramTypeInfos) {
		super(node, Global.fun_type, referenceLevel);
		this.function = function;
		this.returnTypeInfo = returnTypeInfo;
		this.paramTypeInfos = paramTypeInfos;
	}
	
	public FunctionTypeInfo(Node node, Scope scope, String functionName) {
		super(node, Global.fun_type, 0);
		this.function = scope.getFunction(node, functionName);
		this.returnTypeInfo = function.returnTypeInfo;
		this.paramTypeInfos = Helpers.paramTypeInfoArray(function.params);
	}
	
	public FunctionTypeInfo(Node node, int referenceLevel, TypeInfo returnTypeInfo, TypeInfo[] paramTypeInfos) {
		this(node, referenceLevel, null, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public TypeInfo copy(Node node, int newReferenceLevel) {
		return new FunctionTypeInfo(node, newReferenceLevel, function, returnTypeInfo, paramTypeInfos);
	}
	
	@Override
	public boolean canCastTo(Node node, Generator generator, TypeInfo other) {
		return equals(other);
	}
	
	@Override
	public boolean isFunction() {
		return true;
	}
	
	@Override
	public boolean isNonAddressable() {
		return function != null;
	}
	
	@Override
	public boolean isValidForLogicalBinaryOp(Node node, Generator generator, BinaryOpType opType) {
		return false;
	}
	
	@Override
	public boolean isValidForArithmeticBinaryOp(Node node, Generator generator, BinaryOpType opType) {
		return false;
	}
	
	@Override
	public boolean isValidForUnaryOp(Node node, Generator generator, UnaryOpType opType) {
		return false;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionTypeInfo) {
			FunctionTypeInfo other = (FunctionTypeInfo) obj;
			return super.equals(other) && returnTypeInfo.equals(other.returnTypeInfo) && Arrays.equals(paramTypeInfos, other.paramTypeInfos);
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
