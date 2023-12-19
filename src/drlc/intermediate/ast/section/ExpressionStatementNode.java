package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class ExpressionStatementNode extends BasicSectionNode<Scope, Routine> {
	
	public @NonNull ExpressionNode expressionNode;
	
	public ExpressionStatementNode(Node[] parseNodes, @NonNull ExpressionNode expressionNode) {
		super(parseNodes);
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		expressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		expressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		expressionNode.generateIntermediate(this);
	}
}
