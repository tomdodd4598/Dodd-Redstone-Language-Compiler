package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public abstract class ExpressionNode extends ASTNode<Scope, Routine> {
	
	public boolean setTypeInfo = false;
	public boolean setConstantValue = false;
	
	@SuppressWarnings("null")
	public @NonNull DataId dataId = null;
	
	protected ExpressionNode(Node[] parseNodes) {
		super(parseNodes);
	}
	
	public @NonNull TypeInfo getTypeInfo() {
		setTypeInfo();
		
		return getTypeInfoInternal();
	}
	
	protected abstract @NonNull TypeInfo getTypeInfoInternal();
	
	public void setTypeInfo() {
		if (!setTypeInfo) {
			setTypeInfoInternal();
		}
		setTypeInfo = true;
	}
	
	protected abstract void setTypeInfoInternal();
	
	public @Nullable Value getConstantValue() {
		setConstantValue();
		
		return getConstantValueInternal();
	}
	
	protected abstract @Nullable Value getConstantValueInternal();
	
	public void setConstantValue() {
		if (!setConstantValue) {
			setConstantValueInternal();
		}
		setConstantValue = true;
	}
	
	protected abstract void setConstantValueInternal();
	
	public @Nullable ConstantExpressionNode constantExpressionNode() {
		@Nullable Value constantValue = getConstantValue();
		if (constantValue != null) {
			return new ValueExpressionNode(parseNodes, scope, routine, constantValue);
		}
		else {
			return null;
		}
	}
	
	public boolean isValidLvalue() {
		return false;
	}
	
	public boolean getIsLvalue() {
		return false;
	}
	
	public void setIsLvalue() {
		throw error("Attempted to set invalid expression as lvalue!");
	}
	
	public @Nullable Function getDirectFunction() {
		return null;
	}
}
