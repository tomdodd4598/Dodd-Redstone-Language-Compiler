package drlc.intermediate.ast;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.section.ProgramSectionNode;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class ProgramNode extends ASTNode<Scope, Routine> {
	
	public final @NonNull List<ProgramSectionNode<?, ?>> sectionNodes;
	
	public ProgramNode(Node[] parseNodes, @NonNull List<ProgramSectionNode<?, ?>> sectionNodes) {
		super(parseNodes);
		this.sectionNodes = sectionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		for (ProgramSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		for (ProgramSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		for (ProgramSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		for (ProgramSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		for (ProgramSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.foldConstants(this);
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		for (ProgramSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		for (ProgramSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.generateIntermediate(this);
		}
	}
}
