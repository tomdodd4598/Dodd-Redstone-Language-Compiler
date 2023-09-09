package drlc.intermediate.ast.element;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.*;
import drlc.node.Node;

public class VariableDeclaratorNode extends ASTNode {
	
	public final @NonNull VariableModifier variableModifier;
	public final @NonNull String name;
	public final @NonNull TypeNode typeNode;
	
	@SuppressWarnings("null")
	public @NonNull DeclaratorInfo declaratorInfo = null;
	
	public VariableDeclaratorNode(Node[] parseNodes, @NonNull VariableModifier variableModifier, @NonNull String name, @NonNull TypeNode typeNode) {
		super(parseNodes);
		this.variableModifier = variableModifier;
		this.name = name;
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		typeNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		typeNode.declareExpressions(this);
		
		declaratorInfo = new DeclaratorInfo(this, new Variable(name, variableModifier, typeNode.typeInfo));
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		typeNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		typeNode.foldConstants(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		typeNode.generateIntermediate(this);
	}
}
