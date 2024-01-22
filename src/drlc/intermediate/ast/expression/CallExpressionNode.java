package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class CallExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode callerExpressionNode;
	public final @NonNull List<ExpressionNode> argExpressionNodes;
	
	@SuppressWarnings("null")
	public @NonNull FunctionTypeInfo functionTypeInfo = null;
	
	public CallExpressionNode(Source source, @NonNull ExpressionNode callerExpressionNode, @NonNull List<ExpressionNode> argExpressionNodes) {
		super(source);
		this.callerExpressionNode = callerExpressionNode;
		this.argExpressionNodes = argExpressionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		callerExpressionNode.setScopes(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		callerExpressionNode.defineTypes(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		callerExpressionNode.declareExpressions(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		callerExpressionNode.defineExpressions(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineExpressions(this);
		}
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		callerExpressionNode.checkTypes(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.checkTypes(this);
		}
		
		List<TypeInfo> argTypeInfos = functionTypeInfo.getArgTypeInfos();
		int argExpressionCount = argExpressionNodes.size();
		
		for (int i = 0; i < argExpressionCount; ++i) {
			@NonNull TypeInfo argExpressionType = argExpressionNodes.get(i).getTypeInfo();
			TypeInfo argType = argTypeInfos.get(i);
			if (!argExpressionType.canImplicitCastTo(argType)) {
				throw castError("argument", argExpressionType, argType);
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		callerExpressionNode.foldConstants(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.foldConstants(this);
		}
		
		@Nullable ConstantExpressionNode constantExpressionNode = callerExpressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			callerExpressionNode = constantExpressionNode;
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
		if (callerExpressionNode.getDirectFunction() == null) {
			callerExpressionNode.trackFunctions(this);
		}
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		callerExpressionNode.generateIntermediate(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.generateIntermediate(this);
		}
		
		List<DataId> args = new ArrayList<>();
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			args.add(argExpressionNode.dataId);
		}
		
		@NonNull TypeInfo callerExpressionType = callerExpressionNode.getTypeInfo();
		@NonNull DataId callerDataId = routine.addSelfDereferenceAssignmentAction(this, callerExpressionType.getReferenceLevel(), callerExpressionNode.dataId);
		if (callerExpressionType instanceof ClosureTypeInfo) {
			args.add(callerDataId);
			callerDataId = ((ClosureTypeInfo) callerExpressionType).function.value.dataId();
		}
		routine.addCallAction(this, scope, callerExpressionNode.getDirectFunction(), dataId = routine.nextRegId(functionTypeInfo.returnTypeInfo), callerDataId, args);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return functionTypeInfo.returnTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		callerExpressionNode.setTypeInfo(null);
		@NonNull TypeInfo callerExpressionType = callerExpressionNode.getTypeInfo();
		if (callerExpressionType instanceof FunctionTypeInfo) {
			functionTypeInfo = (FunctionTypeInfo) callerExpressionType;
		}
		else if (callerExpressionType instanceof ClosureTypeInfo) {
			functionTypeInfo = ((ClosureTypeInfo) callerExpressionType).function.value.typeInfo;
		}
		else {
			throw error("Attempted to use expression of incompatible type \"%s\" as caller expression!", callerExpressionType);
		}
		
		List<TypeInfo> argTypeInfos = functionTypeInfo.getArgTypeInfos();
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
