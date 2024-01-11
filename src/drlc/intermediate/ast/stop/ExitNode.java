package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class ExitNode extends StopNode {
	
	public @Nullable ExpressionNode expressionNode;
	
	public ExitNode(Node[] parseNodes, @Nullable ExpressionNode expressionNode) {
		super(parseNodes);
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		if (expressionNode != null) {
			expressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		if (expressionNode != null) {
			expressionNode.declareExpressions(this);
		}
		
		scope.definiteLocalReturn = true;
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.checkTypes(this);
			
			@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
			if (!expressionType.canImplicitCastTo(Main.generator.rootReturnTypeInfo)) {
				throw castError("exit value", expressionType, Main.generator.rootReturnTypeInfo);
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.foldConstants(this);
			
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNode = constantExpressionNode;
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.generateIntermediate(this);
			
			routine.addExitAction(this, expressionNode.dataId);
		}
		else {
			routine.addExitAction(this, Main.generator.intValue(0).dataId());
		}
	}
}
