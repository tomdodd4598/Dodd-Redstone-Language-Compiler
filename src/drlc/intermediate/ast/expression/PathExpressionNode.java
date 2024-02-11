package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;

public class PathExpressionNode extends ExpressionNode {
	
	public final @NonNull Path path;
	
	public boolean setInternal = false;
	
	public @Nullable Value<?> value = null;
	
	public @Nullable Variable variable = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public boolean isLvalue = false;
	
	public boolean setDirectFunction = false;
	
	public @Nullable Function directFunction = null;
	
	public PathExpressionNode(Source source, @NonNull Path path) {
		super(source);
		this.path = path;
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
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo.getImmediateCastType();
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
				throw error("Nested function \"%s\" not yet defined in this scope!", path);
			}
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return value;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (scope.pathGet(this, path, (x, name) -> x.constantExists(name, false))) {
			value = scope.pathGet(this, path, (x, name) -> x.getConstant(this, name, false).value);
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
				directFunction = ((FunctionItemValue) value).typeInfo.function;
			}
			setDirectFunction = true;
		}
		return directFunction;
	}
	
	protected void setInternal() {
		if (!setInternal) {
			setConstantValue();
			
			if (value == null) {
				variable = scope.pathGet(this, path, (x, name) -> x.getVariable(this, name, false));
			}
		}
		setInternal = true;
	}
}
