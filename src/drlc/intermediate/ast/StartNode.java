package drlc.intermediate.ast;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class StartNode extends ASTNode<Scope> {
	
	public final @NonNull UnitNode unitNode;
	
	public StartNode(Node[] parseNodes, @NonNull UnitNode unitNode) {
		super(parseNodes);
		this.unitNode = unitNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = Main.rootScope;
		
		unitNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		unitNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = Main.rootRoutine;
		
		unitNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		unitNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		unitNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		unitNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		unitNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		unitNode.generateIntermediate(this);
	}
}
