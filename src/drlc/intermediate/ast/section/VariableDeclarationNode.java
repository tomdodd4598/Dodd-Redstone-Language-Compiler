package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class VariableDeclarationNode extends StaticSectionNode<Scope, Routine> {
	
	public final @NonNull DeclaratorNode declaratorNode;
	public @Nullable ExpressionNode expressionNode;
	
	public VariableDeclarationNode(Node[] parseNodes, @NonNull DeclaratorNode declaratorNode, @Nullable ExpressionNode expressionNode) {
		super(parseNodes);
		this.declaratorNode = declaratorNode;
		this.expressionNode = expressionNode;
		
		if (expressionNode == null) {
			if (declaratorNode.variableModifier._static) {
				throw error("Static variables require an initializer!");
			}
			if (declaratorNode.typeNode == null) {
				throw error("Can not infer type without variable initializer!");
			}
		}
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		if (expressionNode != null) {
			expressionNode.setScopes(this);
		}
		declaratorNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.defineTypes(this);
		}
		declaratorNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		if (expressionNode != null) {
			expressionNode.declareExpressions(this);
		}
		declaratorNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.defineExpressions(this);
		}
		
		if (declaratorNode.typeNode == null) {
			declaratorNode.inferredTypeInfo = expressionNode.getTypeInfo();
		}
		
		declaratorNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.checkTypes(this);
		}
		declaratorNode.checkTypes(this);
		
		if (expressionNode != null) {
			@NonNull TypeInfo expressionType = expressionNode.getTypeInfo(), variableType = declaratorNode.declaratorInfo.getTypeInfo();
			if (!expressionType.canImplicitCastTo(variableType)) {
				throw castError("initialization value", expressionType, variableType);
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.foldConstants(this);
		}
		declaratorNode.foldConstants(this);
		
		if (expressionNode != null) {
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
		declaratorNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		if (expressionNode != null) {
			expressionNode.generateIntermediate(this);
		}
		declaratorNode.generateIntermediate(this);
		
		if (expressionNode != null) {
			routine.addAssignmentAction(this, declaratorNode.declaratorInfo.dataId(), expressionNode.dataId);
		}
	}
}
