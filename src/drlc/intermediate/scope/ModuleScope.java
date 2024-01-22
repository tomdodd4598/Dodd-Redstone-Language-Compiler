package drlc.intermediate.scope;

import org.eclipse.jdt.annotation.Nullable;

import drlc.intermediate.ast.ASTNode;

public class ModuleScope extends Scope {
	
	public ModuleScope(ASTNode<?> node, @Nullable String name, @Nullable Scope parent) {
		super(node, name, parent, false);
		definiteLocalReturn = true;
	}
}
