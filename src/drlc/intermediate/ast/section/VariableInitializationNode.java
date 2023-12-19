package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.VariableDeclaratorNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class VariableInitializationNode extends BasicSectionNode<Scope, Routine> {
	
	public final @NonNull VariableDeclaratorNode declaratorNode;
	public @NonNull ExpressionNode expressionNode;
	
	public VariableInitializationNode(Node[] parseNodes, @NonNull VariableDeclaratorNode declaratorNode, @NonNull ExpressionNode expressionNode) {
		super(parseNodes);
		this.declaratorNode = declaratorNode;
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
		declaratorNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		expressionNode.defineTypes(this);
		declaratorNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		declaratorNode.declareExpressions(this);
		
		scope.addVariable(this, declaratorNode.declaratorInfo.variable, false);
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		expressionNode.checkTypes(this);
		declaratorNode.checkTypes(this);
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo(), variableType = declaratorNode.declaratorInfo.variable.typeInfo;
		if (!expressionType.canImplicitCastTo(variableType)) {
			throw castError("initialization value", expressionType, variableType);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		expressionNode.foldConstants(this);
		declaratorNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		expressionNode.trackFunctions(this);
		declaratorNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		expressionNode.generateIntermediate(this);
		declaratorNode.generateIntermediate(this);
		
		routine.pushCurrentRegId(this);
		
		routine.addStackInitializationAction(this, declaratorNode.declaratorInfo);
	}
}
