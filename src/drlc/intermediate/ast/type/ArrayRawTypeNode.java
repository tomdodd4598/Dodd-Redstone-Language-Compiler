package drlc.intermediate.ast.type;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.ExpressionNode;
import drlc.intermediate.component.type.ArrayTypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class ArrayRawTypeNode extends RawTypeNode {
	
	public final @NonNull TypeNode typeNode;
	public final @NonNull ExpressionNode constantExpressionNode;
	
	public int length;
	
	public ArrayRawTypeNode(Node[] parseNodes, @NonNull TypeNode typeNode, @NonNull ExpressionNode constantExpressionNode) {
		super(parseNodes);
		this.typeNode = typeNode;
		this.constantExpressionNode = constantExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
		constantExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		typeNode.defineTypes(this);
		
		@Nullable Value constantValue = constantExpressionNode.getConstantValue();
		if (constantValue != null && constantValue.typeInfo.canImplicitCastTo(Main.generator.indexTypeInfo)) {
			length = constantValue.intValue(this);
			if (length < 0) {
				throw error("Length of array type can not be negative!");
			}
		}
		else {
			throw error("Length of array type is not a compile-time non-negative \"%s\" constant!", Main.generator.indexTypeInfo);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		typeNode.declareExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		typeNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		typeNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		typeNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		typeNode.generateIntermediate(this);
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeNode.setTypeInfo();
		
		typeInfo = new ArrayTypeInfo(this, 0, typeNode.typeInfo, length);
	}
}
