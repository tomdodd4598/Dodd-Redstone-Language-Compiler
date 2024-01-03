package drlc.intermediate.ast.element;

import java.util.List;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.section.RuntimeSectionNode;
import drlc.intermediate.ast.stop.StopNode;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class ScopeContentsNode extends ASTNode<Scope, Routine> {
	
	public final @NonNull List<RuntimeSectionNode<?, ?>> sectionNodes;
	public final @Nullable StopNode stopNode;
	
	public ScopeContentsNode(Node[] parseNodes, @NonNull List<RuntimeSectionNode<?, ?>> sectionNodes, @Nullable StopNode stopNode) {
		super(parseNodes);
		this.sectionNodes = sectionNodes;
		this.stopNode = stopNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.setScopes(this);
		}
		if (stopNode != null) {
			stopNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.defineTypes(this);
		}
		if (stopNode != null) {
			stopNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.declareExpressions(this);
		}
		if (stopNode != null) {
			stopNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.defineExpressions(this);
		}
		if (stopNode != null) {
			stopNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.checkTypes(this);
		}
		if (stopNode != null) {
			stopNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.foldConstants(this);
		}
		if (stopNode != null) {
			stopNode.foldConstants(this);
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.trackFunctions(this);
		}
		if (stopNode != null) {
			stopNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		for (RuntimeSectionNode<?, ?> sectionNode : sectionNodes) {
			sectionNode.generateIntermediate(this);
		}
		if (stopNode != null) {
			stopNode.generateIntermediate(this);
		}
	}
}
