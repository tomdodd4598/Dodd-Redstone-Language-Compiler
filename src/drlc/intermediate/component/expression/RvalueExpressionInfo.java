package drlc.intermediate.component.expression;

import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class RvalueExpressionInfo extends ExpressionInfo {
	
	public RvalueExpressionInfo() {
		super();
	}
	
	public RvalueExpressionInfo(TypeInfo typeInfo) {
		super(typeInfo);
	}
	
	@Override
	public ExpressionInfo copy(Node node) {
		return new RvalueExpressionInfo(typeInfo);
	}
	
	@Override
	public boolean isLvalue() {
		return false;
	}
	
	@Override
	public boolean isRvalue() {
		return true;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RvalueExpressionInfo) {
			return typeInfo.equals(((RvalueExpressionInfo) obj).typeInfo);
		}
		else {
			return false;
		}
	}
}
