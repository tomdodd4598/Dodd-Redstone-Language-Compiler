package drlc.intermediate.ast.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.node.Node;

public class FunctionTypeNode extends TypeNode {
	
	public final @NonNull List<TypeNode> paramTypeNodes;
	public final @Nullable TypeNode returnTypeNode;
	
	public FunctionTypeNode(Node[] parseNodes, @NonNull List<TypeNode> paramTypeNodes, @Nullable TypeNode returnTypeNode) {
		super(parseNodes);
		this.paramTypeNodes = paramTypeNodes;
		this.returnTypeNode = returnTypeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		for (TypeNode paramTypeNode : paramTypeNodes) {
			paramTypeNode.setScopes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		for (TypeNode paramTypeNode : paramTypeNodes) {
			paramTypeNode.defineTypes(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		for (TypeNode paramTypeNode : paramTypeNodes) {
			paramTypeNode.declareExpressions(this);
		}
		if (returnTypeNode != null) {
			returnTypeNode.declareExpressions(this);
		}
		
		setTypeInfo();
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	protected void setTypeInfoInternal() {
		for (TypeNode paramTypeNode : paramTypeNodes) {
			paramTypeNode.setTypeInfo();
		}
		if (returnTypeNode != null) {
			returnTypeNode.setTypeInfo();
		}
		
		@NonNull TypeInfo returnType = returnTypeNode != null ? returnTypeNode.typeInfo : Main.generator.voidTypeInfo;
		typeInfo = new FunctionPointerTypeInfo(this, 0, returnType, Helpers.map(paramTypeNodes, x -> x.typeInfo));
	}
	
	@Override
	public void collectRawTypes(Set<RawType> rawTypes) {
		
	}
}
