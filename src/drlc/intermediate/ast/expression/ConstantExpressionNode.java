package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.Nullable;

import drlc.node.Node;

public abstract class ConstantExpressionNode extends ExpressionNode {
	
	protected ConstantExpressionNode(Node[] parseNodes) {
		super(parseNodes);
	}
	
	@Override
	public @Nullable ConstantExpressionNode constantExpressionNode() {
		return this;
	}
}
