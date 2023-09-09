package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.ScopeContentsNode;
import drlc.intermediate.scope.StandardScope;
import drlc.node.Node;

public class ScopedSectionNode extends BasicSectionNode {
	
	public final @NonNull ScopeContentsNode scopeContentsNode;
	
	public ScopedSectionNode(Node[] parseNodes, @NonNull ScopeContentsNode scopeContentsNode) {
		super(parseNodes);
		this.scopeContentsNode = scopeContentsNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = new StandardScope(parent.scope);
		
		scopeContentsNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		scopeContentsNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		scopeContentsNode.declareExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		scopeContentsNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		scopeContentsNode.foldConstants(this);
	}
}
