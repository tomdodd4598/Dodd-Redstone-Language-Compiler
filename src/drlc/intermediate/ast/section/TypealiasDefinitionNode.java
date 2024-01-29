package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.scope.Scope;

public class TypeAliasDefinitionNode extends StaticSectionNode<Scope> {
	
	public final @NonNull String name;
	public final @NonNull TypeNode typeNode;
	
	public TypeAliasDefinitionNode(Source source, @NonNull String name, @NonNull TypeNode typeNode) {
		super(source);
		this.name = name;
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		typeNode.defineTypes(this);
		
		typeNode.setTypeInfo();
		
		scope.addTypeAlias(this, name, typeNode.getTypeInfo());
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		// routine.typeDefMap.put(name, typeNode.typeInfo);
	}
}
