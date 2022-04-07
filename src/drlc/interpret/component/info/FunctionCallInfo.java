package drlc.interpret.component.info;

import drlc.interpret.component.info.type.*;
import drlc.node.Node;

public class FunctionCallInfo {
	
	public FunctionTypeInfo typeInfo;
	private int argc = 0;
	
	public FunctionCallInfo() {}
	
	public int getArgumentCount() {
		return argc;
	}
	
	public TypeInfo getNextParamType(Node node) {
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
}
