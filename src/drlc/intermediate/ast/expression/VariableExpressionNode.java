package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class VariableExpressionNode extends ExpressionNode {
	
	public final @NonNull String name;
	
	public @Nullable Value value = null;
	
	public @Nullable Variable variable = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public boolean isLvalue = false;
	
	public boolean setDirectFunction = false;
	
	public @Nullable Function directFunction = null;
	
	public VariableExpressionNode(Node[] parseNodes, @NonNull String name) {
		super(parseNodes);
		this.name = name;
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
		
		setConstantValue();
		
		if (value == null) {
			variable = scope.getVariable(this, name);
		}
		
		setTypeInfo();
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
	
	@SuppressWarnings("null")
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		if (value != null) {
			routine.addValueAssignmentAction(this, dataId = routine.nextRegId(typeInfo), value);
		}
		else {
			if (isLvalue) {
				routine.addAddressVariableAssignmentAction(this, dataId = routine.nextRegId(typeInfo.modifiedReferenceLevel(this, 1)), variable);
			}
			else {
				routine.addVariableAssignmentAction(this, dataId = routine.nextRegId(typeInfo), variable);
			}
		}
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		if (value != null) {
			typeInfo = value.typeInfo;
		}
		else {
			typeInfo = variable.typeInfo;
		}
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return value;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (scope.constantExists(name)) {
			value = scope.getConstant(this, name).value;
		}
		else {
			value = null;
		}
	}
	
	@Override
	public boolean isValidLvalue() {
		return variable != null;
	}
	
	@Override
	public boolean getIsLvalue() {
		return isLvalue;
	}
	
	@Override
	public void setIsLvalue() {
		isLvalue = true;
	}
	
	@Override
	public @Nullable Function getDirectFunction() {
		if (!setDirectFunction) {
			if (value instanceof FunctionItemValue) {
				directFunction = scope.getFunction(this, name);
			}
			setDirectFunction = true;
		}
		return directFunction;
	}
}
