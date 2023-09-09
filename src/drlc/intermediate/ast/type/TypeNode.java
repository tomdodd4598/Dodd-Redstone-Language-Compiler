package drlc.intermediate.ast.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.node.Node;

public class TypeNode extends ASTNode {
	
	public final int referenceLevel;
	public final @NonNull RawTypeNode rawTypeNode;
	
	public boolean setTypeInfo = false;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public TypeNode(Node[] parseNodes, int referenceLevel, @NonNull RawTypeNode rawTypeNode) {
		super(parseNodes);
		this.referenceLevel = referenceLevel;
		this.rawTypeNode = rawTypeNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
		
		rawTypeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		rawTypeNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		rawTypeNode.declareExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		rawTypeNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		rawTypeNode.foldConstants(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		rawTypeNode.generateIntermediate(this);
	}
	
	public void setTypeInfo() {
		if (!setTypeInfo) {
			setTypeInfoInternal();
		}
		setTypeInfo = true;
	}
	
	protected void setTypeInfoInternal() {
		rawTypeNode.setTypeInfo();
		typeInfo = rawTypeNode.typeInfo.copy(this, referenceLevel);
	}
}
