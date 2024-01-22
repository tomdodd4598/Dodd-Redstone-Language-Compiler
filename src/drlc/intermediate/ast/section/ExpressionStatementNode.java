package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.scope.Scope;

public class ExpressionStatementNode extends RuntimeSectionNode<Scope> {
	
	public @NonNull ExpressionNode expressionNode;
	
	public ExpressionStatementNode(Source source, @NonNull ExpressionNode expressionNode) {
		super(source);
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		expressionNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		expressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		expressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		expressionNode.generateIntermediate(this);
	}
}
