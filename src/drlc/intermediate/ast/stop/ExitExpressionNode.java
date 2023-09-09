package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class ExitExpressionNode extends StopNode {
	
	public @NonNull ExpressionNode expressionNode;
	
	public ExitExpressionNode(Node[] parseNodes, @NonNull ExpressionNode expressionNode) {
		super(parseNodes);
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		
		scope.definiteLocalReturn = true;
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		expressionNode.checkTypes(this);
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.canImplicitCastTo(Main.generator.rootReturnTypeInfo)) {
			throw castError("exit value", expressionType, Main.generator.rootReturnTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		expressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		expressionNode.generateIntermediate(this);
		
		routine.addExitValueAction(this);
	}
}
