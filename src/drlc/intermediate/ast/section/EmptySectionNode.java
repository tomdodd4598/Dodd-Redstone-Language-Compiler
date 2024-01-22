package drlc.intermediate.ast.section;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public class EmptySectionNode extends StaticSectionNode<Scope> {
	
	public EmptySectionNode(Source source) {
		super(source);
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		
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
		
	}
}
