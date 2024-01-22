package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.ExpressionNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.Constant;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class ConstantDefinitionNode extends StaticSectionNode<Scope> {
	
	public final @NonNull String name;
	public final @Nullable TypeNode typeNode;
	public final @NonNull ExpressionNode constantExpressionNode;
	
	public ConstantDefinitionNode(Source source, @NonNull String name, @Nullable TypeNode typeNode, @NonNull ExpressionNode constantExpressionNode) {
		super(source);
		this.name = name;
		this.typeNode = typeNode;
		this.constantExpressionNode = constantExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		if (typeNode != null) {
			typeNode.setScopes(this);
		}
		constantExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		if (typeNode != null) {
			typeNode.defineTypes(this);
		}
		
		if (typeNode != null) {
			typeNode.setTypeInfo();
		}
		
		@Nullable Value<?> constantValue = constantExpressionNode.getConstantValue(typeNode == null ? null : typeNode.getTypeInfo());
		if (constantValue != null && (typeNode == null || constantValue.typeInfo.canImplicitCastTo(typeNode.getTypeInfo()))) {
			scope.addConstant(this, new Constant(name, constantValue));
		}
		else {
			if (typeNode == null) {
				throw error("Value of \"%s\" is not a compile-time constant!", name);
			}
			else {
				throw error("Value of \"%s\" is not a compile-time \"%s\" constant!", name, typeNode.getTypeInfo());
			}
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
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
}
