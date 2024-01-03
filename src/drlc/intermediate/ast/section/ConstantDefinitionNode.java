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

public class ConstantDefinitionNode extends StaticSectionNode<Scope, Routine> {
	
	public final @NonNull String name;
	public final @Nullable TypeNode typeNode;
	public final @NonNull ExpressionNode constantExpressionNode;
	
	public ConstantDefinitionNode(Node[] parseNodes, @NonNull String name, @Nullable TypeNode typeNode, @NonNull ExpressionNode constantExpressionNode) {
		super(parseNodes);
		this.name = name;
		this.typeNode = typeNode;
		this.constantExpressionNode = constantExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		if (typeNode != null) {
			typeNode.setScopes(this);
		}
		constantExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		if (typeNode != null) {
			typeNode.defineTypes(this);
		}
		
		if (typeNode != null) {
			typeNode.setTypeInfo();
		}
		
		@Nullable Value constantValue = constantExpressionNode.getConstantValue();
		if (constantValue != null && (typeNode == null || constantValue.typeInfo.canImplicitCastTo(typeNode.typeInfo))) {
			scope.addConstant(this, new Constant(name, constantValue), false);
		}
		else {
			if (typeNode == null) {
				throw error("Value of \"%s\" is not a compile-time constant!", name);
			}
			else {
				throw error("Value of \"%s\" is not a compile-time \"%s\" constant!", name, typeNode.typeInfo);
			}
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
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
}
