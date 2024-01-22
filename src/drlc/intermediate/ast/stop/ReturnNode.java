package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.ast.section.FunctionDefinitionNode;
import drlc.intermediate.component.type.TypeInfo;

public class ReturnNode extends StopNode {
	
	public @Nullable ExpressionNode expressionNode;
	
	public @Nullable FunctionDefinitionNode closureDefinition = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo expectedTypeInfo = null;
	
	public ReturnNode(Source source, @Nullable ExpressionNode expressionNode) {
		super(source);
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
		
		if (routine.equals(Main.rootRoutine)) {
			throw error("Root routine can not return - use an exit statement!");
		}
		
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
		
		if (closureDefinition != null) {
			closureDefinition.function.updateReturnType(expressionNode.getTypeInfo());
		}
		
		expectedTypeInfo = routine.getReturnTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.checkTypes(this);
			
			@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
			if (!expressionType.canImplicitCastTo(expectedTypeInfo)) {
				throw castError("return value", expressionType, expectedTypeInfo);
			}
		}
		else if (!Main.generator.unitTypeInfo.canImplicitCastTo(expectedTypeInfo)) {
			throw castError("return value", Main.generator.unitTypeInfo, expectedTypeInfo);
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
			
			routine.addReturnAction(this, expressionNode.dataId);
		}
		else {
			routine.addReturnAction(this, Main.generator.unitValue.dataId());
		}
	}
}
