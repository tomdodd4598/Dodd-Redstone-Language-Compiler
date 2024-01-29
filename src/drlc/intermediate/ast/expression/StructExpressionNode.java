package drlc.intermediate.ast.expression;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.Helpers.Pair;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.scope.Scope;

public class StructExpressionNode extends ExpressionNode {
	
	public final @NonNull Path path;
	public final @Nullable List<String> labels;
	public final @NonNull List<ExpressionNode> expressionNodes;
	
	@SuppressWarnings("null")
	public @NonNull StructTypeInfo typeInfo = null;
	
	@SuppressWarnings("null")
	public @NonNull List<ExpressionNode> sortedExpressionNodes = null;
	
	public @Nullable StructValue constantValue = null;
	
	public StructExpressionNode(Source source, @NonNull Path path, @NonNull Pair<List<String>, @NonNull List<ExpressionNode>> expressionNodesPair) {
		super(source);
		this.path = path;
		labels = expressionNodesPair.left;
		expressionNodes = expressionNodesPair.right;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.defineExpressions(this);
		}
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.checkTypes(this);
		}
		
		int count = expressionNodes.size();
		for (int i = 0; i < count; ++i) {
			@SuppressWarnings("null") @NonNull TypeInfo expressionType = sortedExpressionNodes.get(i).getTypeInfo(), memberType = typeInfo.typeInfos.get(i);
			if (!expressionType.canImplicitCastTo(memberType)) {
				throw castError("member", expressionType, memberType);
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.foldConstants(this);
		}
		
		int count = expressionNodes.size();
		for (int i = 0; i < count; ++i) {
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNodes.get(i).constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNodes.set(i, constantExpressionNode);
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		for (ExpressionNode expressionNode : expressionNodes) {
			expressionNode.generateIntermediate(this);
		}
		
		@NonNull TypeInfo rawTypeInfo = typeInfo.copy(this);
		routine.addCompoundAssignmentAction(this, dataId = typeInfo.isAddress() ? scope.nextLocalDataId(routine, rawTypeInfo) : routine.nextRegId(rawTypeInfo), Helpers.map(sortedExpressionNodes, x -> x.dataId));
		
		dataId = routine.addSelfAddressAssignmentAction(this, scope, typeInfo.getReferenceLevel(), dataId);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		@NonNull TypeInfo typeInfo = scope.pathGet(this, path, (x, name) -> x.getTypeInfo(this, name, false));
		if (!(typeInfo instanceof StructTypeInfo)) {
			throw error("Type \"%s\" is not a struct type!", typeInfo);
		}
		
		this.typeInfo = (StructTypeInfo) typeInfo;
		
		if (labels != null) {
			int count = expressionNodes.size();
			sortedExpressionNodes = new ArrayList<>(Collections.nCopies(count, null));
			for (int i = 0; i < count; ++i) {
				@SuppressWarnings("null") @NonNull String label = labels.get(i);
				MemberInfo info = typeInfo.getMemberInfo(label);
				if (info == null) {
					throw error("Expression of type \"%s\" has no member \"%s\"!", typeInfo, label);
				}
				sortedExpressionNodes.set(info.index, expressionNodes.get(i));
			}
		}
		else {
			sortedExpressionNodes = expressionNodes;
		}
		
		List<TypeInfo> memberTypeInfos = this.typeInfo.typeInfos;
		int structMemberCount = memberTypeInfos.size(), expressionCount = sortedExpressionNodes.size();
		if (structMemberCount != expressionCount) {
			throw error("Struct expression requires %d members but received %d!", structMemberCount, expressionCount);
		}
		
		for (int i = 0; i < expressionCount; ++i) {
			sortedExpressionNodes.get(i).setTypeInfo(memberTypeInfos.get(i));
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (typeInfo.isAddress()) {
			return;
		}
		
		List<Value<?>> values = new ArrayList<>();
		for (ExpressionNode expressionNode : sortedExpressionNodes) {
			@Nullable Value<?> elementConstantValue = expressionNode.getConstantValue();
			if (elementConstantValue == null) {
				return;
			}
			values.add(elementConstantValue);
		}
		constantValue = new StructValue(this, typeInfo, values);
	}
}
