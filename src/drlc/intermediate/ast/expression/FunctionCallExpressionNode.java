package drlc.intermediate.ast.expression;

import java.util.List;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class FunctionCallExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode expressionNode;
	public final @NonNull List<ExpressionNode> argExpressionNodes;
	
	@SuppressWarnings("null")
	public @NonNull FunctionTypeInfo functionTypeInfo = null;
	
	public FunctionCallExpressionNode(Node[] parseNodes, @NonNull ExpressionNode expressionNode, @NonNull List<ExpressionNode> argExpressionNodes) {
		super(parseNodes);
		this.expressionNode = expressionNode;
		this.argExpressionNodes = argExpressionNodes;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		expressionNode.defineTypes(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.declareExpressions(this);
		}
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		expressionNode.checkTypes(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.checkTypes(this);
		}
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.canImplicitCastTo(functionTypeInfo)) {
			throw castError("function value", expressionType, functionTypeInfo);
		}
		
		List<TypeInfo> paramTypeInfos = functionTypeInfo.paramTypeInfos;
		int functionParamCount = paramTypeInfos.size(), argExpressionCount = argExpressionNodes.size();
		if (functionParamCount != argExpressionCount) {
			throw error("Function call requires %d arguments but received %d!", functionParamCount, argExpressionCount);
		}
		
		for (int i = 0; i < argExpressionCount; ++i) {
			expressionType = argExpressionNodes.get(i).getTypeInfo();
			TypeInfo paramType = paramTypeInfos.get(i);
			if (!expressionType.canImplicitCastTo(paramType)) {
				throw castError("argument value", expressionType, paramType);
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		expressionNode.foldConstants(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.foldConstants(this);
		}
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
		
		int argExpressionCount = argExpressionNodes.size();
		for (int i = 0; i < argExpressionCount; ++i) {
			@Nullable ConstantExpressionNode constantArgExpressionNode = argExpressionNodes.get(i).constantExpressionNode();
			if (constantArgExpressionNode != null) {
				argExpressionNodes.set(i, constantArgExpressionNode);
			}
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		expressionNode.generateIntermediate(this);
		
		routine.pushCurrentRegId(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.generateIntermediate(this);
			
			routine.pushCurrentRegId(this);
		}
		
		routine.incrementRegId(functionTypeInfo.returnTypeInfo);
		routine.addFunctionAction(this, expressionNode.getDirectFunction(), argExpressionNodes.size());
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return functionTypeInfo.returnTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.isFunction()) {
			throw error("Attempted to use expression of incompatible type \"%s\" as function expression!", expressionType);
		}
		
		functionTypeInfo = (FunctionTypeInfo) expressionType;
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
}
