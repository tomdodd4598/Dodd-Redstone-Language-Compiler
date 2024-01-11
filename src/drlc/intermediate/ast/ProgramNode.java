package drlc.intermediate.ast;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.section.StaticSectionNode;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class ProgramNode extends ASTNode<Scope> {
	
	public final @NonNull List<StaticSectionNode<?>> sectionNodes;
	
	public ProgramNode(Node[] parseNodes, @NonNull List<StaticSectionNode<?>> sectionNodes) {
		super(parseNodes);
		this.sectionNodes = sectionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.foldConstants(this);
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		for (StaticSectionNode<?> sectionNode : sectionNodes) {
			sectionNode.generateIntermediate(this);
		}
	}
}
