package drlc.intermediate.ast.type;

import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeDefinition;
import drlc.node.Node;

public class AddressTypeNode extends TypeNode {
	
	public final boolean mutable;
	public final @NonNull TypeNode typeNode;
	
	public AddressTypeNode(Node[] parseNodes, boolean mutable, @NonNull TypeNode typeNode) {
		super(parseNodes);
		this.mutable = mutable;
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		typeNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		typeNode.declareExpressions(this);
		
		setTypeInfo();
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
	
	@Override
	protected void setTypeInfoInternal() {
		typeNode.setTypeInfo();
		typeInfo = typeNode.typeInfo.addressOf(this, mutable);
	}
	
	@Override
	public void collectTypedefs(Set<TypeDefinition> typedefs) {
		
	}
}
