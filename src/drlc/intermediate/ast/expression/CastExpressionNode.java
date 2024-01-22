package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class CastExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode expressionNode;
	public final @NonNull TypeNode typeNode;
	
	public @Nullable Value<?> constantValue = null;
	
	public CastExpressionNode(Source source, @NonNull ExpressionNode expressionNode, @NonNull TypeNode typeNode) {
		super(source);
		this.expressionNode = expressionNode;
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		typeNode.setScopes(this);
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		typeNode.defineTypes(this);
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		typeNode.declareExpressions(this);
		expressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		typeNode.defineExpressions(this);
		expressionNode.defineExpressions(this);
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		typeNode.checkTypes(this);
		expressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		typeNode.foldConstants(this);
		expressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		typeNode.trackFunctions(this);
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		typeNode.generateIntermediate(this);
		expressionNode.generateIntermediate(this);
		
		routine.addTypeCastAction(this, scope, typeNode.getTypeInfo(), expressionNode.getTypeInfo(), dataId = routine.nextRegId(typeNode.getTypeInfo()), expressionNode.dataId);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeNode.getTypeInfo();
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		expressionNode.setTypeInfo(null);
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		@Nullable Value<?> innerConstantValue = expressionNode.getConstantValue();
		if (innerConstantValue != null) {
			constantValue = Main.generator.typeCast(this, typeNode.getTypeInfo(), innerConstantValue);
		}
		else {
			constantValue = null;
		}
	}
}
