package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.*;
import drlc.intermediate.scope.ModuleScope;

public class ModuleDefinitionNode extends StaticSectionNode<ModuleScope> {
	
	public final @NonNull String name;
	public final @NonNull ModuleNode moduleNode;
	
	public ModuleDefinitionNode(Source source, @NonNull String name, @NonNull ModuleNode moduleNode) {
		super(source);
		this.name = name;
		this.moduleNode = moduleNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new ModuleScope(this, name, parent.scope);
		
		moduleNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		moduleNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		moduleNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		moduleNode.defineExpressions(this);
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
	public void trackFunctions(ASTNode<?> parent) {
		moduleNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		moduleNode.generateIntermediate(this);
	}
}
