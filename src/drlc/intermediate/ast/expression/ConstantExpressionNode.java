package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public abstract class ConstantExpressionNode extends ExpressionNode {
	
	protected ConstantExpressionNode(Node[] parseNodes) {
		super(parseNodes);
		setTypeInfo = true;
		setConstantValue = true;
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Value<?> getConstantValue() {
		return super.getConstantValue();
	}
	
	@SuppressWarnings("null")
	@Override
	public @NonNull Value<?> getConstantValue(@Nullable TypeInfo targetType) {
		return super.getConstantValue(targetType);
	}
	
	@Override
	protected abstract @NonNull Value<?> getConstantValueInternal();
	
	@Override
	public @NonNull ConstantExpressionNode constantExpressionNode() {
		return this;
	}
}
