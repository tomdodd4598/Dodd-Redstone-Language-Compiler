package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.ExpressionNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.Constant;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class ConstantDefinitionNode extends BasicSectionNode<Scope, Routine> {
	
	public final @NonNull String name;
	public final @NonNull TypeNode typeNode;
	public final @NonNull ExpressionNode constantExpressionNode;
	
	public ConstantDefinitionNode(Node[] parseNodes, @NonNull String name, @NonNull TypeNode typeNode, @NonNull ExpressionNode constantExpressionNode) {
		super(parseNodes);
		this.name = name;
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
		
		typeNode.setTypeInfo();
		
		@Nullable Value constantValue = constantExpressionNode.getConstantValue();
		if (constantValue != null && constantValue.typeInfo.canImplicitCastTo(typeNode.typeInfo)) {
			scope.addConstant(this, new Constant(name, constantValue), false);
		}
		else {
			throw error("Value of \"%s\" is not a compile-time \"%s\" constant!", name, typeNode.typeInfo);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
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
}
