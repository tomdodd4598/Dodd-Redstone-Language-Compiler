package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class ValueExpressionNode extends ConstantExpressionNode {
	
	public final @NonNull Value value;
	
	public boolean setDirectFunction = false;
	
	public @Nullable Function directFunction = null;
	
	public ValueExpressionNode(Node[] parseNodes, Scope scope, Routine routine, @NonNull Value value) {
		super(parseNodes);
		this.scope = scope;
		this.routine = routine;
		this.value = value;
		setTypeInfo = true;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		if (directFunction != null) {
			routine.onNonLocalFunctionItemExpression(this, directFunction);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		routine.addValueAssignmentAction(this, dataId = routine.nextRegId(value.typeInfo), value);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return value.typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		
	}
	
	@Override
	protected @NonNull Value getConstantValueInternal() {
		return value;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
	
	@Override
	public @Nullable Function getDirectFunction() {
		if (!setDirectFunction) {
			if (value instanceof FunctionItemValue) {
				directFunction = scope.getFunction(this, ((FunctionItemValue) value).name);
			}
			setDirectFunction = true;
		}
		return directFunction;
	}
}
