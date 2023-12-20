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
	public @NonNull TypeInfo baseTypeInfo = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo offsetTypeInfo = null;
	
	public boolean baseArray = false;
	
	public @Nullable Value constantValue = null;
	
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
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		baseExpressionNode.checkTypes(this);
		indexExpressionNode.checkTypes(this);
		
		if (baseArray && baseExpressionNode.isValidLvalue()) {
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
		
		DataId baseDataId;
		if (baseArray && !baseExpressionNode.getIsLvalue()) {
			routine.addAddressAssignmentAction(this, baseDataId = routine.nextRegId(offsetTypeInfo), baseExpressionNode.dataId);
		}
		else {
			baseDataId = baseExpressionNode.dataId;
		}
		
		indexExpressionNode.generateIntermediate(this);
		
		DataId target = routine.nextRegId(offsetTypeInfo);
		routine.addBinaryOpAction(this, offsetTypeInfo, BinaryOpType.PLUS, indexExpressionNode.getTypeInfo(), target, baseDataId, indexExpressionNode.dataId);
		
		if (isLvalue) {
			dataId = target;
		}
		else {
			routine.addDereferenceAssignmentAction(this, dataId = routine.nextRegId(baseTypeInfo), target);
		}
		
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return baseTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		@NonNull TypeInfo expressionType = baseExpressionNode.getTypeInfo();
		if (expressionType.isAddress()) {
			baseTypeInfo = expressionType.modifiedReferenceLevel(this, -1);
			offsetTypeInfo = expressionType;
		}
		else if (expressionType.isArray()) {
			baseTypeInfo = ((ArrayTypeInfo) expressionType).elementTypeInfo;
			offsetTypeInfo = baseTypeInfo.modifiedReferenceLevel(this, 1);
			baseArray = true;
		}
		else {
			throw error("Attempted to use expression of incompatible type \"%s\" as indexable expression!", expressionType);
		}
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		if (!isLvalue) {
			@Nullable Value indexConstantValue = indexExpressionNode.getConstantValue();
			if (indexConstantValue != null) {
				@Nullable Value innerConstantValue = baseExpressionNode.getConstantValue();
				if (innerConstantValue instanceof ArrayValue) {
					constantValue = ((ArrayValue) innerConstantValue).values.get(indexConstantValue.intValue(this));
				}
			}
		}
	}
	
	@Override
	public boolean isValidLvalue() {
		return true;
	}
	
	@Override
	public boolean getIsLvalue() {
		return isLvalue;
	}
	
	@Override
	public void setIsLvalue() {
		isLvalue = true;
	}
}
