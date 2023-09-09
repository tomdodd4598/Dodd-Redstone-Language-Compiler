package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class ReturnExpressionNode extends StopNode {
	
	public @NonNull ExpressionNode expressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo expectedTypeInfo = null;
	
	public ReturnExpressionNode(Node[] parseNodes, @NonNull ExpressionNode expressionNode) {
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
		
		if (routine.isRootRoutine()) {
			throw error("Root routine can not return a value! Use an exit value statement!");
		}
		
		expressionNode.declareExpressions(this);
		
		scope.definiteLocalReturn = true;
		
		expectedTypeInfo = routine.getReturnTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		expressionNode.checkTypes(this);
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.canImplicitCastTo(expectedTypeInfo)) {
			throw castError("return value", expressionType, expectedTypeInfo);
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
		
		routine.addReturnValueAction(this);
	}
}
