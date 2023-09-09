package drlc.intermediate.ast.conditional;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.section.ConditionalSectionNode;
import drlc.intermediate.scope.StandardScope;
import drlc.node.Node;

public class ConditionalElseNode extends ConditionalEndNode {
	
	public final @NonNull ConditionalSectionNode conditionalNode;
	
	public ConditionalElseNode(Node[] parseNodes, @NonNull ConditionalSectionNode conditionalNode) {
		super(parseNodes);
		this.conditionalNode = conditionalNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = new StandardScope(parent.scope);
		
		conditionalNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		conditionalNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		conditionalNode.declareExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		conditionalNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		conditionalNode.foldConstants(this);
	}
}
