package drlc.intermediate.ast;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.routine.RootRoutine;
import drlc.intermediate.scope.StandardScope;
import drlc.node.Node;

public class StartNode extends ASTNode {
	
	public final @NonNull UnitNode unitNode;
	
	public StartNode(Node[] parseNodes, @NonNull UnitNode unitNode) {
		super(parseNodes);
		this.unitNode = unitNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = new StandardScope(null);
		
		Main.generator.addBuiltInTypes(this);
		Main.generator.addBuiltInDirectives(this);
		Main.generator.addBuiltInConstants(this);
		Main.generator.addBuiltInVariables(this);
		Main.generator.addBuiltInFunctions(this);
		
		unitNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		unitNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = new RootRoutine();
		
		unitNode.declareExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		unitNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		unitNode.foldConstants(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		unitNode.generateIntermediate(this);
	}
}
