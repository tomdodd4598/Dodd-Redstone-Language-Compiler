package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class MemberExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode expressionNode;
	public @NonNull String memberName;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public @Nullable Value<?> constantValue = null;
	
	@SuppressWarnings("null")
	public @NonNull MemberInfo memberInfo = null;
	
	public boolean setMemberInfo = false;
	
	public boolean isLvalue = false;
	
	public MemberExpressionNode(Source source, @NonNull ExpressionNode baseExpressionNode, @NonNull String memberName) {
		super(source);
		this.expressionNode = baseExpressionNode;
		this.memberName = memberName;
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
		
		if (expressionNode.isValidLvalue()) {
			expressionNode.setIsLvalue();
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		expressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		expressionNode.foldConstants(this);
		
		if (!isLvalue) {
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNode = constantExpressionNode;
			}
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		expressionNode.generateIntermediate(this);
		
		DataId baseDataId;
		@NonNull TypeInfo expressionTypeInfo = expressionNode.getTypeInfo();
		if (!expressionNode.getIsLvalue()) {
			if (expressionTypeInfo.isAddress()) {
				baseDataId = routine.addSelfDereferenceAssignmentAction(this, expressionTypeInfo.getReferenceLevel() - 1, expressionNode.dataId);
			}
			else {
				routine.addAddressAssignmentAction(this, baseDataId = routine.nextRegId(expressionTypeInfo.copy(this, true)), expressionNode.dataId);
			}
		}
		else {
			baseDataId = routine.addSelfDereferenceAssignmentAction(this, expressionTypeInfo.getReferenceLevel(), expressionNode.dataId);
		}
		
		@NonNull TypeInfo addressTypeInfo = typeInfo.addressOf(this, true);
		DataId target = routine.nextRegId(addressTypeInfo);
		DataId indexId = Main.generator.natValue(getMemberInfo().offset).dataId();
		
		routine.addBinaryOpAction(this, Main.generator.natTypeInfo, BinaryOpType.PLUS, Main.generator.natTypeInfo, target, baseDataId, indexId);
		
		if (isLvalue) {
			dataId = target;
		}
		else {
			routine.addDereferenceAssignmentAction(this, dataId = routine.nextRegId(typeInfo), target);
		}
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		typeInfo = getMemberInfo().typeInfo;
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (!isLvalue) {
			@Nullable Value<?> baseConstantValue = expressionNode.getConstantValue();
			if (baseConstantValue != null && !baseConstantValue.typeInfo.isAddress()) {
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
			expressionNode.setTypeInfo(null);
			@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
			if (!expressionType.isMemberAccessValid()) {
				throw error("Member access not valid for expression of type \"%s\"!", expressionType);
			}
			
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
