package drlc.intermediate.ast.section;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.*;
import drlc.intermediate.scope.ModuleScope;

public class ModuleDeclarationNode extends StaticSectionNode<ModuleScope> {
	
	public final @NonNull String name;
	
	@SuppressWarnings("null")
	public @NonNull ModuleNode moduleNode = null;
	
	public ModuleDeclarationNode(Source source, @NonNull String name) {
		super(source);
		this.name = name;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		String fileName = source.getSubModuleFileName(name);
		try {
			moduleNode = Helpers.getAST(fileName).moduleNode;
		}
		catch (IOException e) {
			throw error("Failed to import module \"%s\"!", fileName);
		}
		
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
