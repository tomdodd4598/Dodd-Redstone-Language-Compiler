package drlc.intermediate.ast;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class UnitNode extends ASTNode<Scope> {
	
	public final @NonNull SetupNode setupNode;
	public final @NonNull ProgramNode programNode;
	
	public UnitNode(Node[] parseNodes, @NonNull SetupNode setupNode, @NonNull ProgramNode programNode) {
		super(parseNodes);
		this.setupNode = setupNode;
		this.programNode = programNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		setupNode.setScopes(this);
		programNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		setupNode.defineTypes(this);
		programNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		setupNode.declareExpressions(this);
		programNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		setupNode.defineExpressions(this);
		programNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		setupNode.checkTypes(this);
		programNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		setupNode.foldConstants(this);
		programNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		setupNode.trackFunctions(this);
		programNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		setupNode.generateIntermediate(this);
		programNode.generateIntermediate(this);
	}
}
