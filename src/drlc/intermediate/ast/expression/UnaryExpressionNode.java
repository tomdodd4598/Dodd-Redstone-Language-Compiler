package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.UnaryOpType;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class UnaryExpressionNode extends ExpressionNode {
	
	public final @NonNull UnaryOpType unaryOpType;
	public @NonNull ExpressionNode expressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public @Nullable Value<?> constantValue = null;
	
	public UnaryExpressionNode(Source source, @NonNull UnaryOpType unaryOpType, @NonNull ExpressionNode expressionNode) {
		super(source);
		this.unaryOpType = unaryOpType;
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		expressionNode.defineExpressions(this);
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		expressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		expressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		expressionNode.generateIntermediate(this);
		routine.addUnaryOpAction(this, unaryOpType, expressionNode.getTypeInfo(), dataId = routine.nextRegId(typeInfo), expressionNode.dataId);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		expressionNode.setTypeInfo(targetType == null ? null : Main.generator.unaryOpInverseTypeInfo(this, targetType, unaryOpType));
		typeInfo = Main.generator.unaryOpTypeInfo(this, unaryOpType, expressionNode.getTypeInfo());
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		@Nullable Value<?> innerConstantValue = expressionNode.getConstantValue();
		if (innerConstantValue != null) {
			constantValue = Main.generator.unaryOp(this, unaryOpType, innerConstantValue);
		}
		else {
			constantValue = null;
		}
	}
	
	@Override
	public boolean isStatic() {
		return expressionNode.isStatic();
	}
}
