package drlc.intermediate.component.expression;

import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class LvalueExpressionInfo extends ExpressionInfo {
	
	public LvalueExpressionInfo() {
		super();
	}
	
	protected LvalueExpressionInfo(TypeInfo typeInfo) {
		super(typeInfo);
	}
	
	@Override
	public ExpressionInfo copy(Node node) {
		return new LvalueExpressionInfo(typeInfo);
	}
	
	@Override
	public boolean isLvalue() {
		return true;
	}
	
	@Override
	public boolean isRvalue() {
		return false;
	}
	
	@Override
	public void incrementReferenceLevel(Node node) {
		throw new IllegalArgumentException(String.format("Reference level increment is not valid for lvalue expressions! %s", node));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LvalueExpressionInfo) {
			return typeInfo.equals(((LvalueExpressionInfo) obj).typeInfo);
		}
		else {
			return false;
		}
	}
}
