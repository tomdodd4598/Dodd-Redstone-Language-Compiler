package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class MemberExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode baseExpressionNode;
	public @NonNull String memberName;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public @Nullable TypeInfo baseTypeInfo = null;
	
	public @Nullable Value constantValue = null;
	
	@SuppressWarnings("null")
	public @NonNull MemberInfo memberInfo = null;
	
	public boolean setMemberInfo = false;
	
	public boolean isLvalue = false;
	
	public MemberExpressionNode(Node[] parseNodes, @NonNull ExpressionNode baseExpressionNode, @NonNull String memberName) {
		super(parseNodes);
		this.baseExpressionNode = baseExpressionNode;
		this.memberName = memberName;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		baseExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		baseExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		baseExpressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		baseExpressionNode.defineExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		baseExpressionNode.checkTypes(this);
		
		if (baseExpressionNode.isValidLvalue()) {
			baseExpressionNode.setIsLvalue();
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		baseExpressionNode.foldConstants(this);
		
		if (!isLvalue) {
			@Nullable ConstantExpressionNode constantExpressionNode = baseExpressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				baseExpressionNode = constantExpressionNode;
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		baseExpressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		baseExpressionNode.generateIntermediate(this);
		
		DataId baseDataId;
		if (!baseExpressionNode.getIsLvalue()) {
			routine.addAddressAssignmentAction(this, baseDataId = routine.nextRegId(baseTypeInfo.modifiedReferenceLevel(this, 1)), baseExpressionNode.dataId);
		}
		else {
			baseDataId = baseExpressionNode.dataId;
		}
		
		DataId baseDataIdIndexed = baseDataId.atOffset(this, getMemberInfo().offset, typeInfo.modifiedReferenceLevel(this, 1));
		if (isLvalue) {
			dataId = baseDataIdIndexed;
		}
		else {
			routine.addDereferenceAssignmentAction(this, dataId = routine.nextRegId(typeInfo), baseDataIdIndexed);
		}
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = getMemberInfo().typeInfo;
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (!isLvalue) {
			@Nullable Value baseConstantValue = baseExpressionNode.getConstantValue();
			if (baseConstantValue != null) {
				@NonNull MemberInfo memberInfo = getMemberInfo();
				constantValue = baseConstantValue.atOffset(this, memberInfo.offset, memberInfo.typeInfo);
			}
		}
	}
	
	@Override
	public boolean isValidLvalue() {
		return true;
	}
	
	@Override
	public boolean isMutableLvalue() {
		return baseExpressionNode.isMutableLvalue();
	}
	
	@Override
	public boolean getIsLvalue() {
		return isLvalue;
	}
	
	@Override
	public void setIsLvalue() {
		isLvalue = true;
	}
	
	public @NonNull MemberInfo getMemberInfo() {
		if (!setMemberInfo) {
			@NonNull TypeInfo expressionType = baseExpressionNode.getTypeInfo();
			@Nullable MemberInfo info = expressionType.getMemberInfo(memberName);
			if (info == null) {
				throw error("Expression of type \"%s\" has no member \"%s\"!", expressionType, memberName);
			}
			else {
				memberInfo = info;
			}
		}
		setMemberInfo = true;
		return memberInfo;
	}
}
