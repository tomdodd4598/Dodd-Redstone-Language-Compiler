package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.ScopeContentsNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.IterativeScope;
import drlc.node.Node;

public class ConditionalIterativeSectionNode extends BasicSectionNode {
	
	public final boolean until;
	public @NonNull ExpressionNode expressionNode;
	public final @NonNull ScopeContentsNode scopedSectionNode;
	
	public ConditionalIterativeSectionNode(Node[] parseNodes, boolean until, @NonNull ExpressionNode expressionNode, @NonNull ScopeContentsNode scopedSectionNode) {
		super(parseNodes);
		this.until = until;
		this.expressionNode = expressionNode;
		this.scopedSectionNode = scopedSectionNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = new IterativeScope(parent.scope, false);
		
		expressionNode.setScopes(this);
		scopedSectionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		expressionNode.defineTypes(this);
		scopedSectionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		scopedSectionNode.declareExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		expressionNode.checkTypes(this);
		scopedSectionNode.checkTypes(this);
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.canImplicitCastTo(Main.generator.boolTypeInfo)) {
			throw castError("conditional value", expressionType, Main.generator.boolTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		expressionNode.foldConstants(this);
		scopedSectionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
}
