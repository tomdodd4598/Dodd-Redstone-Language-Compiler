package drlc.intermediate.ast.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.node.Node;

public class BasicRawTypeNode extends RawTypeNode {
	
	public final @NonNull String name;
	
	public BasicRawTypeNode(Node[] parseNodes, @NonNull String name) {
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
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = scope.getRawType(this, name).getTypeInfo(this, scope, 0);
	}
}
