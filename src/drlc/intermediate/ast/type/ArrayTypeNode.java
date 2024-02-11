package drlc.intermediate.ast.type;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.ExpressionNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.Value;

public class ArrayTypeNode extends TypeNode {
	
	public final @NonNull TypeNode typeNode;
	public final @NonNull ExpressionNode constantExpressionNode;
	
	public int length;
	
	public ArrayTypeNode(Source source, @NonNull TypeNode typeNode, @NonNull ExpressionNode constantExpressionNode) {
		super(source);
		this.typeNode = typeNode;
		this.constantExpressionNode = constantExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
		constantExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		typeNode.defineTypes(this);
		
		@Nullable Value<?> constantValue = constantExpressionNode.getConstantValue(Main.generator.natTypeInfo);
		if (constantValue != null && constantValue.typeInfo.canImplicitCastTo(Main.generator.natTypeInfo)) {
			length = constantValue.intValue(this);
			if (length < 0) {
				throw error("Length of array type can not be negative!");
			}
		}
		else {
			throw error("Length of array type is not a compile-time \"%s\" constant!", Main.generator.natTypeInfo);
		}
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
		
		typeInfo = new ArrayTypeInfo(this, new ArrayList<>(), typeNode.typeInfo, length);
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {
		typeNode.collectTypeDefs(typeDefs);
	}
}
