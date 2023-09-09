package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public class SectionLabelNode extends BasicSectionNode {
	
	public final @NonNull String name;
	
	public SectionLabelNode(Node[] parseNodes, @NonNull String name) {
		super(parseNodes);
		this.name = name;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		
	}
}
