package drlc.intermediate.ast.stop;

import org.eclipse.jdt.annotation.Nullable;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public class BreakNode extends StopNode {
	
	public final @Nullable String label;
	
	public BreakNode(Node[] parseNodes, @Nullable String label) {
		super(parseNodes);
		this.label = label;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		if (!scope.isBreakable(label)) {
			throw error("Can not use break statement in non-iterative section!");
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		routine.addAction(scope.getBreakJump(this, label));
	}
}
