package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.ScopeContentsNode;
import drlc.intermediate.scope.IterativeScope;
import drlc.node.Node;

public class LoopIterativeSectionNode extends IterativeSectionNode {
	
	public final @NonNull ScopeContentsNode bodyNode;
	
	public LoopIterativeSectionNode(Node[] parseNodes, @Nullable String label, @NonNull ScopeContentsNode bodyNode) {
		super(parseNodes, label);
		this.bodyNode = bodyNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = new IterativeScope(parent.scope, label, true);
		
		bodyNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		bodyNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		bodyNode.declareExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		bodyNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		bodyNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		bodyNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		routine.incrementSectionId();
		scope.continueJump.setTarget(routine.currentSectionId());
		bodyNode.generateIntermediate(this);
		
		routine.incrementSectionId();
		scope.breakJump.setTarget(routine.currentSectionId());
	}
}
