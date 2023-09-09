package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.ScopeContentsNode;
import drlc.intermediate.scope.IterativeScope;
import drlc.node.Node;

public class LoopIterativeSectionNode extends BasicSectionNode {
	
	public final @NonNull ScopeContentsNode scopedSectionNode;
	
	public LoopIterativeSectionNode(Node[] parseNodes, @NonNull ScopeContentsNode scopedSectionNode) {
		super(parseNodes);
		this.scopedSectionNode = scopedSectionNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = new IterativeScope(parent.scope, true);
		
		scopedSectionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		scopedSectionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		scopedSectionNode.declareExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		scopedSectionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		scopedSectionNode.foldConstants(this);
	}
}
