package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.VariableDeclaratorNode;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class VariableDeclarationNode extends BasicSectionNode<Scope, Routine> {
	
	public final @NonNull VariableDeclaratorNode declaratorNode;
	
	public VariableDeclarationNode(Node[] parseNodes, @NonNull VariableDeclaratorNode declaratorNode) {
		super(parseNodes);
		this.declaratorNode = declaratorNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		declaratorNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		declaratorNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		declaratorNode.declareExpressions(this);
		
		scope.addVariable(this, declaratorNode.declaratorInfo.variable, false);
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		declaratorNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		declaratorNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		declaratorNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		declaratorNode.generateIntermediate(this);
		
		routine.addDeclarationAction(this, declaratorNode.declaratorInfo);
	}
}
