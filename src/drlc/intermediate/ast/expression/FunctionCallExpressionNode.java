package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class FunctionCallExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode expressionNode;
	public final @NonNull List<ExpressionNode> argExpressionNodes;
	
	@SuppressWarnings("null")
	public @NonNull FunctionTypeInfo functionTypeInfo = null;
	
	public @Nullable ClosureTypeInfo closureTypeInfo = null;
	
	public FunctionCallExpressionNode(Node[] parseNodes, @NonNull ExpressionNode expressionNode, @NonNull List<ExpressionNode> argExpressionNodes) {
		super(parseNodes);
		this.expressionNode = expressionNode;
		this.argExpressionNodes = argExpressionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, parent.scope);
		
		expressionNode.setScopes(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		expressionNode.defineTypes(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		expressionNode.defineExpressions(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineExpressions(this);
		}
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		expressionNode.checkTypes(this);
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.checkTypes(this);
		}
		
		@Nullable TypeInfo expressionType = expressionNode.getTypeInfo();
		
		if (expressionType.isClosure()) {
			closureTypeInfo = (ClosureTypeInfo) expressionType;
			expressionType = expressionType.getFunction();
		}
		
		if (!expressionType.canImplicitCastTo(functionTypeInfo)) {
			throw castError("function value", expressionType, functionTypeInfo);
		}
		
		List<TypeInfo> argTypeInfos = functionTypeInfo.getArgTypeInfos();
		int argExpressionCount = argExpressionNodes.size();
		
		for (int i = 0; i < argExpressionCount; ++i) {
			@NonNull TypeInfo argExpressionType = argExpressionNodes.get(i).getTypeInfo();
			TypeInfo argType = argTypeInfos.get(i);
			if (!argExpressionType.canImplicitCastTo(argType)) {
				throw castError("argument value", argExpressionType, argType);
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
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
	public void trackFunctions(ASTNode<?> parent) {
		if (expressionNode.getDirectFunction() == null) {
			expressionNode.trackFunctions(this);
		}
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		expressionNode.generateIntermediate(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.generateIntermediate(this);
		}
		
		List<DataId> args = new ArrayList<>();
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			args.add(argExpressionNode.dataId);
		}
		
		@NonNull DataId functionDataId = closureTypeInfo == null ? expressionNode.dataId : closureTypeInfo.function.value.dataId();
		
		if (closureTypeInfo != null) {
			args.add(expressionNode.dataId);
		}
		
		routine.addFunctionAction(this, expressionNode.getDirectFunction(), dataId = routine.nextRegId(functionTypeInfo.returnTypeInfo), functionDataId, args, scope);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return functionTypeInfo.returnTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		expressionNode.setTypeInfo(null);
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		FunctionTypeInfo functionTypeInfo = expressionType.getFunction();
		if (functionTypeInfo == null) {
			throw error("Attempted to use expression of incompatible type \"%s\" as function expression!", expressionType);
		}
		
		this.functionTypeInfo = functionTypeInfo;
		
		List<TypeInfo> argTypeInfos = this.functionTypeInfo.getArgTypeInfos();
		int functionArgCount = argTypeInfos.size(), argExpressionCount = argExpressionNodes.size();
		if (functionArgCount != argExpressionCount) {
			throw error("Function call requires %d arguments but received %d!", functionArgCount, argExpressionCount);
		}
		
		for (int i = 0; i < argExpressionCount; ++i) {
			argExpressionNodes.get(i).setTypeInfo(argTypeInfos.get(i));
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
}
