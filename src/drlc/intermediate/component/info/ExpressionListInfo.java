package drlc.intermediate.component.info;

import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public abstract class ExpressionListInfo {
	
	public ExpressionListInfo() {}
	
	public abstract boolean isFunctionCallInfo();
	
	public abstract int getListCount();
	
	public abstract TypeInfo getNextType(Node node);
	
	public abstract RuntimeException typeCastError(Node node, TypeInfo lastTypeInfo, TypeInfo expectedTypeInfo);
}
