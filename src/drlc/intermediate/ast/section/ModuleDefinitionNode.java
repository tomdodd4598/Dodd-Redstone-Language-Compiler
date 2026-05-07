package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.ast.*;
import drlc.intermediate.module.ModuleOrigin;
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
		if (!parent.scope.isModule) {
			throw error("Module definitions are only allowed in module scope!");
		}
		
		ModuleScope parentModuleScope = (ModuleScope) parent.scope.getCurrentModule();
		Pair<String, String> moduleFile = Helpers.resolveSubModuleFilePair(this, parentModuleScope, name);
		String parentFileName = moduleFile.left, fileName = moduleFile.right;
		Helpers.registerModuleFileParent(this, fileName, parentFileName);
		
		ModuleScope cachedScope = Main.fileScopeMap.get(fileName);
		if (cachedScope != null) {
			ModuleOrigin origin = Main.fileOriginMap.get(fileName);
			if (origin == ModuleOrigin.FILE) {
				throw error("Module \"%s\" is already imported from file and can not also be defined inline!", fileName);
			}
			else if (origin == ModuleOrigin.ROOT) {
				throw error("Module \"%s\" already occupies the root module slot!", fileName);
			}
			else {
				throw error("Module \"%s\" is already defined inline!", fileName);
			}
		}
		
		scope = new ModuleScope(this, name, parentModuleScope);
		Helpers.registerModuleFile(fileName, scope, ModuleOrigin.INLINE);
		
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
		routine = parent.routine;
		
		moduleNode.declareFunctions(this);
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
