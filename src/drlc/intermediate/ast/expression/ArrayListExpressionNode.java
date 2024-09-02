package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.scope.Scope;

public class ArrayListExpressionNode extends ExpressionNode {
	
	public final @NonNull List<ExpressionNode> expressionNodes;
	
	public final int length;
	
	@SuppressWarnings("null")
	public @NonNull ArrayTypeInfo typeInfo = null;
	
	public @Nullable ArrayValue constantValue = null;
	
	public ArrayListExpressionNode(Source source, @NonNull List<ExpressionNode> expressionNodes) {
		super(source);
		this.expressionNodes = expressionNodes;
		length = expressionNodes.size();
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.defineExpressions(this);
		}
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.foldConstants(this);
		}
		
		for (int i = 0; i < length; ++i) {
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNodes.get(i).constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNodes.set(i, constantExpressionNode);
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.generateIntermediate(this);
		}
		
		routine.addCompoundAssignmentAction(this, dataId = routine.nextRegId(typeInfo), Helpers.map(expressionNodes, x -> x.dataId));
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
		
		@Nullable TypeInfo elementTargetType = arrayTargetType == null ? null : arrayTargetType.elementTypeInfo;
		for (int i = 0; i < length; ++i) {
			expressionNodes.get(i).setTypeInfo(elementTargetType);
		}
		
		List<TypeInfo> expressionTypes = Helpers.map(expressionNodes, ExpressionNode::getTypeInfo);
		
		if (elementTargetType == null) {
			elementTargetType = Helpers.getCommonTypeInfo(this, expressionTypes);
		}
		
		if (elementTargetType != null) {
			for (ExpressionNode expressionNode : expressionNodes) {
				@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
				if (!expressionType.canImplicitCastTo(elementTargetType)) {
					throw castError("array element", expressionType, elementTargetType);
				}
			}
			typeInfo = new ArrayTypeInfo(this, new ArrayList<>(), elementTargetType, length);
		}
		else {
			throw error("Can not determine element type of array %s!", Helpers.arrayString(expressionTypes));
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		List<Value<?>> values = new ArrayList<>();
		for (ExpressionNode expressionNode : expressionNodes) {
			@Nullable Value<?> elementConstantValue = expressionNode.getConstantValue();
			if (elementConstantValue == null) {
				return;
			}
			values.add(elementConstantValue);
		}
		constantValue = new ArrayValue(this, typeInfo, values);
	}
	
	@Override
	public boolean isStatic() {
		return expressionNodes.stream().allMatch(ExpressionNode::isStatic);
	}
}
