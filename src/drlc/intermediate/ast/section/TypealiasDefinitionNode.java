package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class TypealiasDefinitionNode extends StaticSectionNode<Scope, Routine> {
	
	public final @NonNull String name;
	public final @NonNull TypeNode typeNode;
	
	public TypealiasDefinitionNode(Node[] parseNodes, @NonNull String name, @NonNull TypeNode typeNode) {
		super(parseNodes);
		this.name = name;
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		typeNode.defineTypes(this);
		
		typeNode.setTypeInfo();
		
		scope.addAliasType(this, name, typeNode.typeInfo);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		// routine.typedefMap.put(name, typeNode.typeInfo);
	}
}
