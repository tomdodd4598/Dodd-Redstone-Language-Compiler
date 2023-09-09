package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.BinaryOpType;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class IndexExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode expressionNode;
	public @NonNull ExpressionNode indexExpressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo offsetTypeInfo = null;
	
	public boolean innerArray = false;
	
	public @Nullable Value constantValue = null;
	
	public boolean isLvalue = false;
	
	public IndexExpressionNode(Node[] parseNodes, @NonNull ExpressionNode expressionNode, @NonNull ExpressionNode indexExpressionNode) {
		super(parseNodes);
		this.expressionNode = expressionNode;
		this.indexExpressionNode = indexExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
		indexExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode parent) {
		expressionNode.defineTypes(this);
		indexExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		indexExpressionNode.declareExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode parent) {
		expressionNode.checkTypes(this);
		indexExpressionNode.checkTypes(this);
		
		if (innerArray && expressionNode.isValidLvalue()) {
			expressionNode.setIsLvalue();
		}
	}
	
	@Override
	public void foldConstants(ASTNode parent) {
		expressionNode.foldConstants(this);
		indexExpressionNode.foldConstants(this);
		
		if (!isLvalue) {
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNode = constantExpressionNode;
			}
		}
		
		@Nullable ConstantExpressionNode constantIndexExpressionNode = indexExpressionNode.constantExpressionNode();
		if (constantIndexExpressionNode != null) {
			indexExpressionNode = constantIndexExpressionNode;
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode parent) {
		expressionNode.generateIntermediate(this);
		
		if (innerArray && !expressionNode.getIsLvalue()) {
			routine.pushCurrentRegId(this);
			
			routine.incrementRegId(offsetTypeInfo);
			routine.addAddressOfStackAssignmentAction(this);
		}
		
		routine.pushCurrentRegId(this);
		
		indexExpressionNode.generateIntermediate(this);
		
		routine.pushCurrentRegId(this);
		
		routine.incrementRegId(offsetTypeInfo);
		routine.addBinaryOpAction(this, offsetTypeInfo, BinaryOpType.PLUS, indexExpressionNode.getTypeInfo());
		
		if (!isLvalue) {
			routine.pushCurrentRegId(this);
			
			routine.incrementRegId(typeInfo);
			routine.addDereferenceAction(this);
		}
		
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (expressionType.isAddress()) {
			typeInfo = expressionType.modifiedReferenceLevel(this, -1);
			offsetTypeInfo = expressionType;
		}
		else if (expressionType.isArray()) {
			typeInfo = ((ArrayTypeInfo) expressionType).elementTypeInfo;
			offsetTypeInfo = typeInfo.modifiedReferenceLevel(this, 1);
			innerArray = true;
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
				@Nullable Value innerConstantValue = expressionNode.getConstantValue();
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
