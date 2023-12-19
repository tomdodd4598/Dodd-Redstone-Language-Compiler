package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public abstract class ConstantExpressionNode extends ExpressionNode {
	
	protected ConstantExpressionNode(Node[] parseNodes) {
		super(parseNodes);
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Value getConstantValue() {
		return super.getConstantValue();
	}
	
	@Override
	protected abstract @NonNull Value getConstantValueInternal();
	
	@Override
	public @NonNull ConstantExpressionNode constantExpressionNode() {
		return this;
	}
}
