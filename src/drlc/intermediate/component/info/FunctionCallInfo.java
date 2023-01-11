package drlc.intermediate.component.info;

import drlc.intermediate.component.type.*;
import drlc.node.Node;

public class FunctionCallInfo extends ExpressionListInfo {
	
	public FunctionTypeInfo typeInfo;
	private int argc = 0;
	
	public FunctionCallInfo() {
		super();
	}
	
	@Override
	public boolean isFunctionCallInfo() {
		return true;
	}
	
	@Override
	public int getListCount() {
		return argc;
	}
	
	@Override
	public TypeInfo getNextType(Node node) {
		TypeInfo[] paramTypeInfos = typeInfo.paramTypeInfos;
		if (argc >= paramTypeInfos.length) {
			throw new IllegalArgumentException(String.format("Function requires %d arguments but received more! %s", paramTypeInfos.length, node));
		}
		else {
			TypeInfo info = paramTypeInfos[argc];
			++argc;
			return info;
		}
	}
	
	@Override
	public RuntimeException typeCastError(Node node, TypeInfo lastTypeInfo, TypeInfo expectedTypeInfo) {
		return new IllegalArgumentException(String.format("Attempted to use expression of type \"%s\" as function argument of incompatible type \"%s\"! %s", lastTypeInfo, expectedTypeInfo, node));
	}
}
