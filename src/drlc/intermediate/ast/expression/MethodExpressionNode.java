package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class MethodExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode receiverExpressionNode;
	public final @NonNull Path path;
	public final @NonNull List<ExpressionNode> argExpressionNodes;
	
	public boolean setInternal = false;
	
	@SuppressWarnings("null")
	public @NonNull Function function = null;
	
	public int referenceLevelDiff;
	
	public MethodExpressionNode(Source source, @NonNull ExpressionNode receiverExpressionNode, @NonNull Path path, @NonNull List<ExpressionNode> argExpressionNodes) {
		super(source);
		this.receiverExpressionNode = receiverExpressionNode;
		this.path = path;
		this.argExpressionNodes = argExpressionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		receiverExpressionNode.setScopes(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		receiverExpressionNode.defineTypes(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		receiverExpressionNode.declareExpressions(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		setInternal();
		
		receiverExpressionNode.defineExpressions(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineExpressions(this);
		}
		
		setTypeInfo(null);
		
		if (receiverExpressionNode.isValidLvalue()) {
			receiverExpressionNode.setIsLvalue();
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		receiverExpressionNode.checkTypes(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.checkTypes(this);
		}
		
		List<TypeInfo> paramTypeInfos = function.paramTypeInfos;
		TypeInfo firstParamTypeInfo = paramTypeInfos.get(0), receiverTypeInfo = receiverExpressionNode.getTypeInfo();
		referenceLevelDiff = firstParamTypeInfo.getReferenceLevel() - receiverTypeInfo.getReferenceLevel();
		
		boolean isReceiverMutable = receiverExpressionNode.isMutable();
		TypeInfo firstArgTypeInfo = receiverTypeInfo.modifyReferenceLevel(parent, referenceLevelDiff, isReceiverMutable);
		if (!firstArgTypeInfo.canImplicitCastTo(firstParamTypeInfo)) {
			if (!isReceiverMutable && firstParamTypeInfo.isMutableReference()) {
				throw error("Attempted to use immutable expression of type \"%s\" as method receiver of function with first argument of mutable reference type \"%s\"!", receiverTypeInfo, firstParamTypeInfo);
			}
			else {
				throw error("Attempted to use expression of type \"%s\" as method receiver of function with first argument of type \"%s\"!", receiverTypeInfo, firstParamTypeInfo);
			}
		}
		
		int argExpressionCount = argExpressionNodes.size();
		for (int i = 0; i < argExpressionCount; ++i) {
			@NonNull TypeInfo argExpressionType = argExpressionNodes.get(i).getTypeInfo();
			TypeInfo paramType = paramTypeInfos.get(i + 1);
			if (!argExpressionType.canImplicitCastTo(paramType)) {
				throw castError("method argument", argExpressionType, paramType);
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		receiverExpressionNode.foldConstants(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.foldConstants(this);
		}
		
		@Nullable ConstantExpressionNode constantExpressionNode = receiverExpressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			receiverExpressionNode = constantExpressionNode;
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
		receiverExpressionNode.trackFunctions(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		receiverExpressionNode.generateIntermediate(this);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.generateIntermediate(this);
		}
		
		List<DataId> args = new ArrayList<>();
		
		boolean receiverLvalue = receiverExpressionNode.getIsLvalue();
		DataId receiverExpressionDataId = receiverExpressionNode.dataId;
		int referenceLevelModifier = receiverLvalue ? 1 : 0;
		if (referenceLevelDiff > 0) {
			DataId temp;
			if (receiverLvalue) {
				temp = receiverExpressionDataId;
			}
			else {
				temp = scope.nextLocalDataId(routine, receiverExpressionNode.getTypeInfo());
				routine.addAssignmentAction(this, temp, receiverExpressionDataId);
			}
			args.add(routine.addSelfAddressAssignmentAction(this, scope, referenceLevelDiff - referenceLevelModifier, temp));
		}
		else {
			args.add(routine.addSelfDereferenceAssignmentAction(this, -referenceLevelDiff + referenceLevelModifier, receiverExpressionDataId));
		}
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			args.add(argExpressionNode.dataId);
		}
		
		routine.addCallAction(this, scope, function, dataId = routine.nextRegId(function.returnTypeInfo), function.value.dataId(), args);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return function.returnTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		setInternal();
		
		List<TypeInfo> paramTypeInfos = function.paramTypeInfos;
		int functionParamCount = paramTypeInfos.size(), argExpressionCount = argExpressionNodes.size();
		
		if (functionParamCount == 0) {
			throw error("Function requiring zero arguments is not valid for method call!");
		}
		else if (functionParamCount - 1 != argExpressionCount) {
			throw error("Method call requires %d arguments but received %d!", functionParamCount - 1, argExpressionCount);
		}
		
		receiverExpressionNode.setTypeInfo(null);
		
		for (int i = 0; i < argExpressionCount; ++i) {
			argExpressionNodes.get(i).setTypeInfo(paramTypeInfos.get(i + 1));
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
	
	protected void setInternal() {
		if (!setInternal) {
			function = scope.pathGet(this, path, (x, name) -> x.getFunction(this, name, false));
		}
		setInternal = true;
	}
}
