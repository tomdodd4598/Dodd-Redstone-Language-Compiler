package drlc.intermediate.ast.expression;

import java.util.*;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class ArrayListExpressionNode extends ExpressionNode {
	
	public final @NonNull List<ExpressionNode> expressionNodes;
	
	public final int length;
	
	@SuppressWarnings("null")
	public @NonNull ArrayTypeInfo typeInfo = null;
	
	public @Nullable ArrayValue constantValue = null;
	
	public ArrayListExpressionNode(Node[] parseNodes, @NonNull List<ExpressionNode> expressionNodes) {
		super(parseNodes);
		this.expressionNodes = expressionNodes;
		length = expressionNodes.size();
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.declareExpressions(this);
		}
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
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
	public void trackFunctions(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.generateIntermediate(this);
			
			routine.pushCurrentRegId(this);
		}
		
		routine.incrementRegId(typeInfo);
		routine.addStackCompoundAssignmentAction(this, length);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		List<TypeInfo> expressionTypes = expressionNodes.stream().map(ExpressionNode::getTypeInfo).collect(Collectors.toList());
		@Nullable TypeInfo elementTypeInfo = Helpers.getCommonTypeInfo(expressionTypes);
		if (elementTypeInfo != null) {
			for (ExpressionNode expressionNode : expressionNodes) {
				@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
				if (!expressionType.canImplicitCastTo(elementTypeInfo)) {
					throw castError("array element", expressionType, elementTypeInfo);
				}
			}
			typeInfo = new ArrayTypeInfo(this, 0, elementTypeInfo, length);
		}
		else {
			throw error("Can not determine common type of element types %s!", Helpers.arrayString(expressionTypes));
		}
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (length == 0) {
			constantValue = Main.generator.emptyArrayValue;
			return;
		}
		
		List<Value> values = new ArrayList<>();
		for (ExpressionNode expressionNode : expressionNodes) {
			@Nullable Value elementConstantValue = expressionNode.getConstantValue();
			if (elementConstantValue != null) {
				values.add(elementConstantValue);
			}
			else {
				constantValue = null;
				return;
			}
		}
		constantValue = new ArrayValue(this, typeInfo, values);
	}
}
