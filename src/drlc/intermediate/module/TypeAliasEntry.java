package drlc.intermediate.module;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.section.TypeAliasDefinitionNode;
import drlc.intermediate.component.type.TypeInfo;

public class TypeAliasEntry implements TypeEntry {
	
	public final @NonNull String name;
	
	protected final @NonNull TypeAliasDefinitionNode definitionNode;
	
	protected boolean defining = false, defined = false;
	protected boolean resolving = false, resolved = false;
	
	@SuppressWarnings("null")
	protected @NonNull TypeInfo typeInfo = null;
	
	public TypeAliasEntry(@NonNull String name, @NonNull TypeAliasDefinitionNode definitionNode) {
		this.name = name;
		this.definitionNode = definitionNode;
	}
	
	@Override
	public @NonNull TypeInfo getTypeInfo(ASTNode<?> node) {
		if (!defined) {
			if (defining) {
				throw Helpers.nodeError(node, "Type alias \"%s\" is circular!", name);
			}
			defining = true;
			try {
				definitionNode.typeNode.defineTypes(definitionNode);
				defined = true;
			}
			finally {
				defining = false;
			}
		}
		if (!resolved) {
			if (resolving) {
				throw Helpers.nodeError(node, "Type alias \"%s\" is circular!", name);
			}
			resolving = true;
			try {
				typeInfo = definitionNode.typeNode.getTypeInfo();
				resolved = true;
			}
			finally {
				resolving = false;
			}
		}
		return typeInfo;
	}
}
