package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.scope.Scope;

public class CallExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode callerExpressionNode;
	public final @NonNull List<ExpressionNode> argExpressionNodes;
	
	@SuppressWarnings("null")
	public @NonNull FunctionTypeInfo functionTypeInfo = null;
	public @Nullable StructConstructorTypeInfo structConstructorTypeInfo = null;
	public @Nullable StructValue constantValue = null;
	
	public CallExpressionNode(Source source, @NonNull ExpressionNode callerExpressionNode, @NonNull List<ExpressionNode> argExpressionNodes) {
		super(source);
		this.callerExpressionNode = callerExpressionNode;
		this.argExpressionNodes = argExpressionNodes;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, false);
		
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
		
		setTypeInfo = false;
		setTypeInfo(null);
		
		for (ExpressionNode argExpressionNode : argExpressionNodes) {
			argExpressionNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		if (structConstructorTypeInfo == null) {
			callerExpressionNode.checkTypes(this);
		}
		
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
	public void generateIntermediate(ASTNode<?> parent) {
		if (structConstructorTypeInfo != null) {
			for (ExpressionNode argExpressionNode : argExpressionNodes) {
				argExpressionNode.generateIntermediate(this);
			}
			
			List<DataId> args = new ArrayList<>();
			for (ExpressionNode argExpressionNode : argExpressionNodes) {
				args.add(argExpressionNode.dataId);
			}
			
			@NonNull StructTypeInfo typeInfo = structConstructorTypeInfo.structTypeInfo;
			@NonNull TypeInfo rawTypeInfo = typeInfo.copy(this);
			routine.addCompoundAssignmentAction(this, dataId = typeInfo.isAddress() ? scope.nextLocalDataId(routine, rawTypeInfo) : routine.nextRegId(rawTypeInfo), args);
			
			dataId = routine.addSelfAddressAssignmentAction(this, scope, typeInfo.getReferenceLevel(), dataId);
			return;
		}
		
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
		if (callerExpressionType instanceof ClosureTypeInfo closureTypeInfo) {
			if (closureTypeInfo.count > 0) {
				args.add(callerDataId);
			}
			callerDataId = closureTypeInfo.function.value.dataId();
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
		if (callerExpressionType instanceof StructConstructorTypeInfo constructorTypeInfo) {
			structConstructorTypeInfo = constructorTypeInfo;
			functionTypeInfo = constructorTypeInfo;
		}
		else if (callerExpressionNode instanceof PathExpressionNode && callerExpressionType instanceof StructTypeInfo structTypeInfo && structTypeInfo.count == 0 && callerExpressionNode.getConstantValue() instanceof StructValue structValue && structValue.count == 0) {
			structConstructorTypeInfo = new StructConstructorTypeInfo(this, structValue.typeInfo);
			functionTypeInfo = structConstructorTypeInfo;
		}
		else if (callerExpressionType instanceof FunctionTypeInfo) {
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
			throw error("%s requires %d arguments but received %d!", structConstructorTypeInfo == null ? "Function call" : "Struct constructor", functionArgCount, argExpressionCount);
		}
		
		for (int i = 0; i < argExpressionCount; ++i) {
			argExpressionNodes.get(i).setTypeInfo(argTypeInfos.get(i));
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (structConstructorTypeInfo != null) {
			List<Value<?>> values = new ArrayList<>();
			for (ExpressionNode argExpressionNode : argExpressionNodes) {
				@Nullable Value<?> value = argExpressionNode.getConstantValue();
				if (value == null) {
					return;
				}
				values.add(value);
			}
			constantValue = new StructValue(this, structConstructorTypeInfo.structTypeInfo, values);
		}
	}
	
	@Override
	public boolean isStatic() {
		return callerExpressionNode.isStatic() && argExpressionNodes.stream().allMatch(ExpressionNode::isStatic);
	}
	
	protected @Nullable ClosureExpressionNode getWrappedClosureExpression(@NonNull ExpressionNode expressionNode) {
		if (expressionNode instanceof ClosureExpressionNode closureExpressionNode) {
			return closureExpressionNode;
		}
		else if (expressionNode instanceof AddressExpressionNode addressExpressionNode) {
			return getWrappedClosureExpression(addressExpressionNode.expressionNode);
		}
		else if (expressionNode instanceof DereferenceExpressionNode dereferenceExpressionNode) {
			return getWrappedClosureExpression(dereferenceExpressionNode.expressionNode);
		}
		else if (expressionNode instanceof CastExpressionNode castExpressionNode) {
			return getWrappedClosureExpression(castExpressionNode.expressionNode);
		}
		else {
			return null;
		}
	}
}
