package drlc.intermediate.ast;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.node.Node;

public class SetupNode extends ASTNode {
	
	public final @NonNull List<DirectiveNode> directiveNodes;
	
	public SetupNode(Node[] parseNodes, @NonNull List<DirectiveNode> directiveNodes) {
		super(parseNodes);
		this.directiveNodes = directiveNodes;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
		
		for (DirectiveNode directiveNode : directiveNodes) {
			directiveNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		for (DirectiveNode directiveNode : directiveNodes) {
			directiveNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		for (DirectiveNode directiveNode : directiveNodes) {
			directiveNode.declareExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		for (DirectiveNode directiveNode : directiveNodes) {
			directiveNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		for (DirectiveNode directiveNode : directiveNodes) {
			directiveNode.foldConstants(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		for (DirectiveNode directiveNode : directiveNodes) {
			directiveNode.generateIntermediate(this);
		}
	}
}
