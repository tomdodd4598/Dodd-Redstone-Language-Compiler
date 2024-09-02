package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;

public abstract class ConstantExpressionNode extends ExpressionNode {
	
	protected ConstantExpressionNode(Source source) {
		super(source);
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
	
	@Override
	public boolean isStatic() {
		return true;
	}
}
