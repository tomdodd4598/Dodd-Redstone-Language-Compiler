package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.scope.Scope;

public class ArrayRepeatExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode repeatExpressionNode;
	public final @NonNull ExpressionNode constantExpressionNode;
	
	public int length;
	
	@SuppressWarnings("null")
	public @NonNull ArrayTypeInfo typeInfo = null;
	
	public @Nullable ArrayValue constantValue = null;
	
	public ArrayRepeatExpressionNode(Source source, @NonNull ExpressionNode repeatExpressionNode, @NonNull ExpressionNode constantExpressionNode) {
		super(source);
		this.repeatExpressionNode = repeatExpressionNode;
		this.constantExpressionNode = constantExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		constantExpressionNode.setScopes(this);
		repeatExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		@Nullable Value<?> constantValue = constantExpressionNode.getConstantValue(Main.generator.natTypeInfo);
		if (constantValue != null && constantValue.typeInfo.canImplicitCastTo(Main.generator.natTypeInfo)) {
			length = constantValue.intValue(this);
			if (length < 0) {
				throw error("Length of array can not be negative!");
			}
		}
		else {
			throw error("Length of array is not a compile-time \"%s\" constant!", Main.generator.natTypeInfo);
		}
		
		repeatExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		repeatExpressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		repeatExpressionNode.defineExpressions(this);
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		repeatExpressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		repeatExpressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantRepeatExpressionNode = repeatExpressionNode.constantExpressionNode();
		if (constantRepeatExpressionNode != null) {
			repeatExpressionNode = constantRepeatExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		repeatExpressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		repeatExpressionNode.generateIntermediate(this);
		
		routine.addCompoundAssignmentAction(this, dataId = routine.nextRegId(typeInfo), Collections.nCopies(length, repeatExpressionNode.dataId));
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		ArrayTypeInfo arrayTargetType = null;
		boolean invalidTargetType = false;
		if (targetType != null) {
			if (targetType.isArray()) {
				arrayTargetType = (ArrayTypeInfo) targetType;
				if (arrayTargetType.length != length) {
					invalidTargetType = true;
				}
			}
			else {
				invalidTargetType = true;
			}
		}
		
		if (invalidTargetType) {
			throw error("Attempted to use array of length %d as expression of incompatible type \"%s\"!", length, targetType);
		}
		
		repeatExpressionNode.setTypeInfo(arrayTargetType == null ? null : arrayTargetType.elementTypeInfo);
		
		typeInfo = new ArrayTypeInfo(this, new ArrayList<>(), repeatExpressionNode.getTypeInfo(), length);
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		@Nullable Value<?> repeatConstantValue = repeatExpressionNode.getConstantValue();
		if (repeatConstantValue != null) {
			constantValue = new ArrayValue(this, typeInfo, Collections.nCopies(length, repeatConstantValue));
		}
		else if (length == 0) {
			constantValue = new ArrayValue(this, typeInfo, new ArrayList<>());
		}
		else {
			constantValue = null;
		}
	}
	
	@Override
	public boolean isStatic() {
		return repeatExpressionNode.isStatic() && constantExpressionNode.isStatic();
	}
}
