package drlc.intermediate.ast.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;

public class TupleTypeNode extends TypeNode {
	
	public final @NonNull List<TypeNode> typeNodes;
	
	public TupleTypeNode(Source source, @NonNull List<TypeNode> typeNodes) {
		super(source);
		this.typeNodes = typeNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		for (TypeNode typeNode : typeNodes) {
			typeNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (TypeNode typeNode : typeNodes) {
			typeNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (TypeNode typeNode : typeNodes) {
			typeNode.declareExpressions(this);
		}
		
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
		for (TypeNode typeNode : typeNodes) {
			typeNode.setTypeInfo();
		}
		
		typeInfo = new TupleTypeInfo(this, new ArrayList<>(), Helpers.map(typeNodes, x -> x.typeInfo));
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {
		for (TypeNode typeNode : typeNodes) {
			typeNode.collectTypeDefs(typeDefs);
		}
	}
}
