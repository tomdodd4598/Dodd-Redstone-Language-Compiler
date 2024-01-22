package drlc.intermediate.ast.section;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.UseTreeNode;
import drlc.intermediate.scope.Scope;

public class UseDeclarationNode extends StaticSectionNode<Scope> {
	
	public final @NonNull UseTreeNode useTreeNode;
	
	public UseDeclarationNode(Source source, @NonNull UseTreeNode useTreeNode) {
		super(source);
		this.useTreeNode = useTreeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		useTreeNode.buildPath(new ArrayList<>());
		
		useTreeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		useTreeNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		useTreeNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		useTreeNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		useTreeNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		useTreeNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		useTreeNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		useTreeNode.generateIntermediate(this);
	}
}
