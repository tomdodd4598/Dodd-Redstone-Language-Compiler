package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.conditional.*;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.ConditionalScope;
import drlc.node.Node;

public class ConditionalSectionNode extends BasicSectionNode {
	
	public final boolean unless;
	public @NonNull ExpressionNode expressionNode;
	public final @NonNull ConditionalStartNode conditionalStartNode;
	public final @Nullable ConditionalEndNode conditionalEndNode;
	
	public ConditionalSectionNode(Node[] parseNodes, boolean unless, @NonNull ExpressionNode expressionNode, @NonNull ConditionalStartNode conditionalStartNode, @Nullable ConditionalEndNode conditionalEndNode) {
		super(parseNodes);
		this.unless = unless;
		this.expressionNode = expressionNode;
		this.conditionalStartNode = conditionalStartNode;
		this.conditionalEndNode = conditionalEndNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = new ConditionalScope(parent.scope, conditionalEndNode != null);
		
		expressionNode.setScopes(this);
		conditionalStartNode.setScopes(this);
		if (conditionalEndNode != null) {
			conditionalEndNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		expressionNode.defineTypes(this);
		conditionalStartNode.defineTypes(this);
		if (conditionalEndNode != null) {
			conditionalEndNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		conditionalStartNode.declareExpressions(this);
		if (conditionalEndNode != null) {
			conditionalEndNode.declareExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		expressionNode.checkTypes(this);
		conditionalStartNode.checkTypes(this);
		if (conditionalEndNode != null) {
			conditionalEndNode.checkTypes(this);
		}
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.canImplicitCastTo(Main.generator.boolTypeInfo)) {
			throw castError("conditional value", expressionType, Main.generator.boolTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		expressionNode.foldConstants(this);
		conditionalStartNode.foldConstants(this);
		if (conditionalEndNode != null) {
			conditionalEndNode.foldConstants(this);
		}
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
}
