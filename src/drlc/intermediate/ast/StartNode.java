package drlc.intermediate.ast;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.scope.Scope;

public class StartNode extends ASTNode<Scope> {
	
	public final @NonNull ModuleNode moduleNode;
	
	public StartNode(Source source, @NonNull ModuleNode moduleNode) {
		super(source);
		this.moduleNode = moduleNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = Main.rootScope;
		
		moduleNode.setScopes(this);
	}
	
	@Override
	public void declareImports(ASTNode<?> parent) {
		moduleNode.declareImports(this);
	}
	
	@Override
	public void declareTypes(ASTNode<?> parent) {
		moduleNode.declareTypes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		moduleNode.defineTypes(this);
	}
	
	@Override
	public void declareFunctions(ASTNode<?> parent) {
		routine = Main.rootRoutine;
		
		moduleNode.declareFunctions(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = Main.rootRoutine;
		
		moduleNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		moduleNode.defineExpressions(this);
	}
	
	@Override
	public void checkImports(ASTNode<?> parent) {
		moduleNode.checkImports(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		moduleNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		moduleNode.foldConstants(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		moduleNode.generateIntermediate(this);
	}
}
