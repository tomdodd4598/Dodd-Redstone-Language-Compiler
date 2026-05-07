package drlc.intermediate.ast.section;

import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.ast.*;
import drlc.intermediate.module.ModuleOrigin;
import drlc.intermediate.scope.ModuleScope;

public class ModuleDeclarationNode extends StaticSectionNode<ModuleScope> {
	
	public final @NonNull String name;
	
	@SuppressWarnings("null")
	public @NonNull ModuleNode moduleNode = null;
	
	protected boolean traverseImportedModule = true;
	
	public ModuleDeclarationNode(Source source, @NonNull String name) {
		super(source);
		this.name = name;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		if (!parent.scope.isModule) {
			throw error("Module declarations are only allowed in module scope!");
		}
		
		ModuleScope parentModuleScope = (ModuleScope) parent.scope.getCurrentModule();
		Pair<String, String> moduleFile = Helpers.resolveSubModuleFilePair(this, parentModuleScope, name);
		String parentFileName = moduleFile.left, fileName = moduleFile.right;
		Helpers.registerModuleFileParent(this, fileName, parentFileName);
		
		ModuleScope cachedScope = Main.fileScopeMap.get(fileName);
		if (cachedScope != null) {
			ModuleOrigin origin = Main.fileOriginMap.get(fileName);
			if (origin == ModuleOrigin.INLINE) {
				throw error("Module \"%s\" is already defined inline and can not be imported from file!", fileName);
			}
			else if (origin == ModuleOrigin.ROOT) {
				throw error("Module \"%s\" already occupies the root module slot!", fileName);
			}
			
			scope = cachedScope;
			parent.scope.addModule(this, name, scope);
			traverseImportedModule = false;
			return;
		}
		
		try {
			StartNode ast = Main.fileASTMap.get(fileName);
			if (ast == null) {
				ast = Helpers.getAST(fileName);
				Main.fileASTMap.put(fileName, ast);
			}
			moduleNode = ast.moduleNode;
		}
		catch (IOException e) {
			throw Helpers.nodeError(this, e, "Failed to import module \"%s\"!", name);
		}
		
		scope = new ModuleScope(this, name, parentModuleScope);
		Helpers.registerModuleFile(fileName, scope, ModuleOrigin.FILE);
		
		traverseImportedModule = Main.activeFileSet.add(fileName);
		if (!traverseImportedModule) {
			return;
		}
		
		try {
			moduleNode.setScopes(this);
		}
		finally {
			Main.activeFileSet.remove(fileName);
		}
	}
	
	@Override
	public void declareImports(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.declareImports(this);
		}
	}
	
	@Override
	public void declareTypes(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.declareTypes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareFunctions(ASTNode<?> parent) {
		routine = parent.routine;
		
		if (traverseImportedModule) {
			moduleNode.declareFunctions(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		if (traverseImportedModule) {
			moduleNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkImports(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.checkImports(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.foldConstants(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		if (traverseImportedModule) {
			moduleNode.generateIntermediate(this);
		}
	}
}
