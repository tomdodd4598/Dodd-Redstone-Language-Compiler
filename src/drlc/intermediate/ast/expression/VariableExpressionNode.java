package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class VariableExpressionNode extends ExpressionNode {
	
	public final @NonNull String name;
	
	public boolean setInternal = false;
	
	public @Nullable Value<?> value = null;
	
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
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		setInternal();
		
		setTypeInfo(null);
		
		if (variable != null) {
			variable = scope.captureVariable(this, variable);
			
			if (!isLvalue && !scope.isVariableDefinitelyInitialized(variable)) {
				throw Helpers.nodeError(parent, "Attempted to use potentially uninitialized variable \"%s\"!", variable.name);
			}
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		Function directFunction = getDirectFunction();
		if (directFunction != null) {
			routine.onNonLocalFunctionItemExpression(this, directFunction);
		}
	}
	
	@SuppressWarnings("null")
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		@NonNull TypeInfo typeInfo = getTypeInfo();
		
		if (value != null) {
			routine.addValueAssignmentAction(this, dataId = routine.nextRegId(typeInfo), value);
		}
		else {
			if (isLvalue) {
				routine.addAddressVariableAssignmentAction(this, dataId = routine.nextRegId(typeInfo.addressOf(this, true)), variable);
			}
			else {
				routine.addVariableAssignmentAction(this, dataId = routine.nextRegId(typeInfo), variable);
			}
		}
	}
	
	protected void setInternal() {
		if (!setInternal) {
			setConstantValue();
			
			if (value == null) {
				variable = scope.getVariable(this, name);
			}
		}
		setInternal = true;
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo instanceof FunctionItemTypeInfo ? ((FunctionItemTypeInfo) typeInfo).functionPointerTypeInfo : typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		setInternal();
		
		if (value != null) {
			typeInfo = value.typeInfo;
		}
		else {
			typeInfo = variable.typeInfo;
		}
		
		if (typeInfo instanceof FunctionItemTypeInfo) {
			if (!((FunctionItemTypeInfo) typeInfo).function.defined) {
				throw error("Nested function \"%s\" not yet defined in this scope!", name);
			}
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return value;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (scope.constantExists(name, false)) {
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
	public boolean isMutableLvalue() {
		return variable != null && variable.modifier.mutable;
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
	public void checkIsAssignable(ASTNode<?> parent) {
		if (!isMutableLvalue()) {
			if (scope.isVariablePotentiallyInitialized(variable)) {
				throw Helpers.nodeError(parent, "Attempted to potentially assign twice to immutable variable \"%s\"!", variable.name);
			}
		}
	}
	
	@Override
	public void initialize(ASTNode<?> parent) {
		if (variable != null) {
			scope.onVariableInitialization(parent, variable);
		}
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
