package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.BinaryOpType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class IndexExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode baseExpressionNode;
	public @NonNull ExpressionNode indexExpressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo addressTypeInfo = null;
	
	public @Nullable ArrayTypeInfo baseArrayTypeInfo = null;
	
	public boolean baseIsArray = false;
	
	public @Nullable Value constantValue = null;
	
	public @Nullable Integer constantIndex = null;
	
	public boolean setConstantIndex = false;
	
	public boolean isLvalue = false;
	
	public IndexExpressionNode(Node[] parseNodes, @NonNull ExpressionNode baseExpressionNode, @NonNull ExpressionNode indexExpressionNode) {
		super(parseNodes);
		this.baseExpressionNode = baseExpressionNode;
		this.indexExpressionNode = indexExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		baseExpressionNode.setScopes(this);
		indexExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		baseExpressionNode.defineTypes(this);
		indexExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		baseExpressionNode.declareExpressions(this);
		indexExpressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		baseExpressionNode.defineExpressions(this);
		indexExpressionNode.defineExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		baseExpressionNode.checkTypes(this);
		indexExpressionNode.checkTypes(this);
		
		if (baseIsArray && baseExpressionNode.isValidLvalue()) {
			baseExpressionNode.setIsLvalue();
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		baseExpressionNode.foldConstants(this);
		indexExpressionNode.foldConstants(this);
		
		if (!isLvalue) {
			@Nullable ConstantExpressionNode constantExpressionNode = baseExpressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				baseExpressionNode = constantExpressionNode;
			}
		}
		
		@Nullable ConstantExpressionNode constantIndexExpressionNode = indexExpressionNode.constantExpressionNode();
		if (constantIndexExpressionNode != null) {
			indexExpressionNode = constantIndexExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		baseExpressionNode.trackFunctions(this);
		indexExpressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		baseExpressionNode.generateIntermediate(this);
		
		boolean constantArrayIndex = baseIsArray && setConstantIndex();
		
		DataId baseDataId;
		if (baseIsArray && !baseExpressionNode.getIsLvalue()) {
			routine.addAddressAssignmentAction(this, baseDataId = routine.nextRegId(constantArrayIndex ? baseArrayTypeInfo.modifiedReferenceLevel(this, 1) : addressTypeInfo), baseExpressionNode.dataId);
		}
		else {
			baseDataId = baseExpressionNode.dataId;
		}
		
		if (constantArrayIndex) {
			if (constantIndex >= baseArrayTypeInfo.length) {
				throw error("Attempted to index array value of type \"%s\" at position %d!", baseArrayTypeInfo, constantIndex);
			}
			
			DataId baseDataIdIndexed = baseDataId.atOffset(this, baseArrayTypeInfo.indexToOffsetShallow(this, constantIndex), addressTypeInfo);
			if (isLvalue) {
				dataId = baseDataIdIndexed;
			}
			else {
				routine.addDereferenceAssignmentAction(this, dataId = routine.nextRegId(typeInfo), baseDataIdIndexed);
			}
		}
		else {
			indexExpressionNode.generateIntermediate(this);
			
			DataId target = routine.nextRegId(addressTypeInfo);
			routine.addBinaryOpAction(this, addressTypeInfo, BinaryOpType.PLUS, indexExpressionNode.getTypeInfo(), target, baseDataId, indexExpressionNode.dataId);
			
			if (isLvalue) {
				dataId = target;
			}
			else {
				routine.addDereferenceAssignmentAction(this, dataId = routine.nextRegId(typeInfo), target);
			}
		}
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		@NonNull TypeInfo baseExpressionType = baseExpressionNode.getTypeInfo();
		if (baseExpressionType.isAddress()) {
			typeInfo = baseExpressionType.modifiedReferenceLevel(this, -1);
			addressTypeInfo = baseExpressionType;
		}
		else if (baseExpressionType.isArray()) {
			baseArrayTypeInfo = (ArrayTypeInfo) baseExpressionType;
			typeInfo = baseArrayTypeInfo.elementTypeInfo;
			addressTypeInfo = typeInfo.modifiedReferenceLevel(this, 1);
			baseIsArray = true;
		}
		else {
			throw error("Attempted to use expression of incompatible type \"%s\" as indexable expression!", baseExpressionType);
		}
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (setConstantIndex() && !isLvalue) {
			@Nullable Value baseConstantValue = baseExpressionNode.getConstantValue();
			if (baseConstantValue instanceof ArrayValue) {
				ArrayTypeInfo arrayTypeInfo = (ArrayTypeInfo) baseConstantValue.typeInfo;
				constantValue = baseConstantValue.atOffset(this, arrayTypeInfo.indexToOffsetShallow(this, constantIndex), arrayTypeInfo.elementTypeInfo);
			}
		}
	}
	
	@Override
	public boolean isValidLvalue() {
		return true;
	}
	
	@Override
	public boolean isMutableLvalue() {
		return baseIsArray ? baseExpressionNode.isMutableLvalue() : baseExpressionNode.getTypeInfo().IS_MUTABLE_REFERENCE;
	}
	
	@Override
	public boolean getIsLvalue() {
		return isLvalue;
	}
	
	@Override
	public void setIsLvalue() {
		isLvalue = true;
	}
	
	protected boolean setConstantIndex() {
		if (!setConstantIndex) {
			@Nullable Value indexConstantValue = indexExpressionNode.getConstantValue();
			if (indexConstantValue != null) {
				constantIndex = indexConstantValue.intValue(this);
			}
		}
		setConstantIndex = true;
		return constantIndex != null;
	}
}
