package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public abstract class ExpressionNode extends ASTNode<Scope> {
	
	public boolean setTypeInfo = false;
	public boolean setConstantValue = false;
	
	@SuppressWarnings("null")
	public @NonNull DataId dataId = null;
	
	protected ExpressionNode(Source source) {
		super(source);
	}
	
	public @NonNull TypeInfo getTypeInfo() {
		if (!setTypeInfo) {
			throw error("Attempted to get type info before it is set!");
		}
		return getTypeInfoInternal();
	}
	
	protected abstract @NonNull TypeInfo getTypeInfoInternal();
	
	public void setTypeInfo(@Nullable TypeInfo targetType) {
		if (!setTypeInfo) {
			setTypeInfoInternal(targetType);
			
			@NonNull TypeInfo typeInfo = getTypeInfoInternal();
			if (targetType != null && !typeInfo.canImplicitCastTo(targetType)) {
				throw castError("value", typeInfo, targetType);
			}
		}
		setTypeInfo = true;
	}
	
	protected abstract void setTypeInfoInternal(@Nullable TypeInfo targetType);
	
	public @Nullable Value<?> getConstantValue() {
		return getConstantValue(setTypeInfo ? getTypeInfo() : null);
	}
	
	public @Nullable Value<?> getConstantValue(@Nullable TypeInfo targetType) {
		setTypeInfo(targetType);
		setConstantValue();
		return getConstantValueInternal();
	}
	
	protected abstract @Nullable Value<?> getConstantValueInternal();
	
	public void setConstantValue() {
		if (!setConstantValue) {
			setConstantValueInternal();
		}
		setConstantValue = true;
	}
	
	protected abstract void setConstantValueInternal();
	
	public @Nullable ConstantExpressionNode constantExpressionNode() {
		@Nullable Value<?> constantValue = getConstantValue();
		if (constantValue != null) {
			return new ValueExpressionNode(source, scope, routine, constantValue);
		}
		else {
			return null;
		}
	}
	
	public abstract boolean isStatic();
	
	public boolean isValidLvalue() {
		return false;
	}
	
	public boolean isMutableLvalue() {
		return false;
	}
	
	public boolean isMutableReference() {
		return getTypeInfo().isMutableReference();
	}
	
	public boolean isMutable() {
		return !getIsLvalue() || isMutableLvalue();
	}
	
	public boolean getIsLvalue() {
		return false;
	}
	
	public void setIsLvalue() {
		throw error("Attempted to set invalid expression as lvalue!");
	}
	
	public void checkIsAssignable(ASTNode<?> parent) {
		if (!isMutableLvalue()) {
			throw Helpers.nodeError(parent, "Attempted to assign to immutable lvalue expression!");
		}
	}
	
	public void initialize(ASTNode<?> parent) {
		
	}
	
	public @Nullable Function getDirectFunction() {
		return null;
	}
}
