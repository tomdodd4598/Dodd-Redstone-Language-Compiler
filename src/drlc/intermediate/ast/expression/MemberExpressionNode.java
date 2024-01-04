package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class MemberExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode expressionNode;
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
		this.expressionNode = baseExpressionNode;
		this.memberName = memberName;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		expressionNode.defineExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		expressionNode.checkTypes(this);
		
		if (expressionNode.isValidLvalue()) {
			expressionNode.setIsLvalue();
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		expressionNode.foldConstants(this);
		
		if (!isLvalue) {
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNode = constantExpressionNode;
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		expressionNode.generateIntermediate(this);
		
		DataId baseDataId;
		if (!expressionNode.getIsLvalue()) {
			routine.addAddressAssignmentAction(this, baseDataId = routine.nextRegId(baseTypeInfo.addressOf(this, true)), expressionNode.dataId);
		}
		else {
			baseDataId = expressionNode.dataId;
		}
		
		DataId baseDataIdIndexed = baseDataId.atOffset(this, getMemberInfo().offset, typeInfo.addressOf(this, true));
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
			@Nullable Value baseConstantValue = expressionNode.getConstantValue();
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
		return expressionNode.isMutableLvalue();
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
			@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
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
