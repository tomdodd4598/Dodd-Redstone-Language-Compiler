package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.IterativeScope;
import drlc.node.Node;

public class LoopIterativeSectionNode extends IterativeSectionNode {
	
	public final @NonNull ScopedBodyNode bodyNode;
	
	public LoopIterativeSectionNode(Node[] parseNodes, @Nullable String label, @NonNull ScopedBodyNode bodyNode) {
		super(parseNodes, label);
		this.bodyNode = bodyNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new IterativeScope(this, parent.scope, true, label);
		
		bodyNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		bodyNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		bodyNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		bodyNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		bodyNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		bodyNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		bodyNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		routine.incrementSectionId();
		scope.continueJump.setTarget(routine.currentSectionId());
		bodyNode.generateIntermediate(this);
		
		routine.incrementSectionId();
		scope.breakJump.setTarget(routine.currentSectionId());
	}
}
