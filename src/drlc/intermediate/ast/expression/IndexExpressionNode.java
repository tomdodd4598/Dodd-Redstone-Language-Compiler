package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.BinaryOpType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.scope.Scope;

public class IndexExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode baseExpressionNode;
	public @NonNull ExpressionNode indexExpressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo addressTypeInfo = null;
	
	public @Nullable ArrayTypeInfo baseArrayTypeInfo = null;
	
	public boolean baseIsArray = false;
	
	public @Nullable Value<?> constantValue = null;
	
	public @Nullable Long constantIndex = null;
	
	public boolean setConstantIndex = false;
	
	public boolean isLvalue = false;
	
	public IndexExpressionNode(Source source, @NonNull ExpressionNode baseExpressionNode, @NonNull ExpressionNode indexExpressionNode) {
		super(source);
		this.baseExpressionNode = baseExpressionNode;
		this.indexExpressionNode = indexExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, false);
		
		baseExpressionNode.setScopes(this);
		indexExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		baseExpressionNode.defineTypes(this);
		indexExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		baseExpressionNode.declareExpressions(this);
		indexExpressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		setTypeInfo(null);
		
		baseExpressionNode.defineExpressions(this);
		indexExpressionNode.defineExpressions(this);
		
		if (baseIsArray && !baseExpressionNode.getTypeInfo().isAddress() && baseExpressionNode.isValidLvalue()) {
			baseExpressionNode.setIsLvalue();
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		baseExpressionNode.checkTypes(this);
		indexExpressionNode.checkTypes(this);
		
		@NonNull TypeInfo indexType = indexExpressionNode.getTypeInfo();
		if (!indexType.canImplicitCastTo(Main.generator.natTypeInfo)) {
			throw castError("index value", indexType, Main.generator.natTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
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
	public void generateIntermediate(ASTNode<?> parent) {
		baseExpressionNode.generateIntermediate(this);
		
		boolean constantArrayIndex = baseIsArray && setConstantIndex();
		
		DataId baseDataId = baseIsArray ? arrayBaseDataId(constantArrayIndex) : baseExpressionNode.dataId;
		
		if (constantArrayIndex) {
			if (!constantIndexInBounds()) {
				throw error("Attempted to index array value of type \"%s\" at position %s!", baseArrayTypeInfo, Long.toUnsignedString(constantIndex));
			}
		}
		else {
			indexExpressionNode.generateIntermediate(this);
		}
		
		DataId target = routine.nextRegId(addressTypeInfo);
		DataId indexId;
		@NonNull TypeInfo indexTypeInfo;
		if (constantArrayIndex) {
			indexId = Main.generator.natValue(constantIndex).dataId();
			indexTypeInfo = Main.generator.natTypeInfo;
		}
		else {
			indexId = indexExpressionNode.dataId;
			indexTypeInfo = indexExpressionNode.getTypeInfo();
		}
		
		routine.addBinaryOpAction(this, addressTypeInfo, BinaryOpType.PLUS, indexTypeInfo, target, baseDataId, indexId);
		
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
		baseExpressionNode.setTypeInfo(null);
		@NonNull TypeInfo baseExpressionType = baseExpressionNode.getTypeInfo();
		if (baseExpressionType.isAddress()) {
			TypeInfo dereferencedBaseType = baseExpressionType.dereference(this, baseExpressionType.getReferenceLevel());
			if (dereferencedBaseType.isArray()) {
				baseArrayTypeInfo = (ArrayTypeInfo) dereferencedBaseType;
				typeInfo = baseArrayTypeInfo.elementTypeInfo;
				addressTypeInfo = typeInfo.addressOf(this, true);
				baseIsArray = true;
			}
			else {
				typeInfo = baseExpressionType.dereference(this, 1);
				addressTypeInfo = baseExpressionType;
			}
		}
		else if (baseExpressionType.isArray()) {
			baseArrayTypeInfo = (ArrayTypeInfo) baseExpressionType;
			typeInfo = baseArrayTypeInfo.elementTypeInfo;
			addressTypeInfo = typeInfo.addressOf(this, true);
			baseIsArray = true;
		}
		else {
			throw error("Attempted to use expression of incompatible type \"%s\" as indexable expression!", baseExpressionType);
		}
		indexExpressionNode.setTypeInfo(Main.generator.natTypeInfo);
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (baseIsArray && setConstantIndex() && !isLvalue && constantIndexInBounds()) {
			@Nullable Value<?> baseConstantValue = baseExpressionNode.getConstantValue();
			if (baseConstantValue instanceof ArrayValue) {
				constantValue = baseConstantValue.atIndex(this, constantIndex.intValue());
			}
		}
	}
	
	@Override
	public boolean isStatic() {
		return baseExpressionNode.isStatic() && indexExpressionNode.isStatic();
	}
	
	@Override
	public boolean isValidLvalue() {
		return true;
	}
	
	@Override
	public boolean isMutableLvalue() {
		if (!baseIsArray) {
			return baseExpressionNode.isMutableReference();
		}
		@NonNull TypeInfo baseExpressionType = baseExpressionNode.getTypeInfo();
		return baseExpressionType.isAddress() ? baseExpressionType.canMutablyDereference() : baseExpressionNode.isMutableLvalue();
	}
	
	@Override
	public boolean getIsLvalue() {
		return isLvalue;
	}
	
	@Override
	public void setIsLvalue() {
		isLvalue = true;
	}
	
	@Override
	public void checkIsReadable(ASTNode<?> parent) {
		baseExpressionNode.checkIsReadable(parent);
	}
	
	protected boolean setConstantIndex() {
		if (!setConstantIndex) {
			@Nullable Value<?> indexConstantValue = indexExpressionNode.getConstantValue(Main.generator.natTypeInfo);
			if (indexConstantValue != null) {
				constantIndex = indexConstantValue.longValue(this);
			}
		}
		setConstantIndex = true;
		return constantIndex != null;
	}
	
	protected boolean constantIndexInBounds() {
		return baseIsArray && constantIndex != null && Long.compareUnsigned(constantIndex, Integer.toUnsignedLong(baseArrayTypeInfo.length)) < 0;
	}
	
	protected @NonNull DataId arrayBaseDataId(boolean constantArrayIndex) {
		@NonNull TypeInfo baseExpressionType = baseExpressionNode.getTypeInfo();
		if (baseExpressionType.isAddress()) {
			int dereferenceLevel = baseExpressionType.getReferenceLevel() - (baseExpressionNode.getIsLvalue() ? 0 : 1);
			return routine.addSelfDereferenceAssignmentAction(this, dereferenceLevel, baseExpressionNode.dataId);
		}
		if (!baseExpressionNode.getIsLvalue()) {
			DataId temp = scope.nextLocalDataId(routine, baseExpressionType);
			routine.addAssignmentAction(this, temp, baseExpressionNode.dataId);
			DataId baseDataId = routine.nextRegId(constantArrayIndex ? baseArrayTypeInfo.addressOf(this, true) : addressTypeInfo);
			routine.addAddressAssignmentAction(this, baseDataId, temp);
			return baseDataId;
		}
		return baseExpressionNode.dataId;
	}
}
