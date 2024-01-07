package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class ReturnNode extends StopNode {
	
	public @Nullable ExpressionNode expressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo expectedTypeInfo = null;
	
	public ReturnNode(Node[] parseNodes, @Nullable ExpressionNode expressionNode) {
		super(parseNodes);
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		if (expressionNode != null) {
			expressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		if (routine.isRootRoutine()) {
			throw error("Root routine can not return - use an exit statement!");
		}
		
		if (expressionNode != null) {
			expressionNode.declareExpressions(this);
		}
		
		scope.definiteLocalReturn = true;
		
		expectedTypeInfo = routine.getReturnTypeInfo();
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.checkTypes(this);
			
			@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
			if (!expressionType.canImplicitCastTo(expectedTypeInfo)) {
				throw castError("return value", expressionType, expectedTypeInfo);
			}
		}
		else if (!Main.generator.voidTypeInfo.canImplicitCastTo(expectedTypeInfo)) {
			throw castError("return value", Main.generator.voidTypeInfo, expectedTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.foldConstants(this);
			
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNode = constantExpressionNode;
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.generateIntermediate(this);
			
			routine.addReturnAction(this, expressionNode.dataId);
		}
		else {
			routine.addReturnAction(this, Main.generator.unitValue.dataId());
		}
	}
}
