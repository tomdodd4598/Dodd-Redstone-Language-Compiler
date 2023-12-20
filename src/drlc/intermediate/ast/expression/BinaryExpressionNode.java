package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.BinaryOpType;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class BinaryExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode leftExpressionNode;
	public final @NonNull BinaryOpType binaryOpType;
	public @NonNull ExpressionNode rightExpressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public @Nullable Value constantValue = null;
	
	public BinaryExpressionNode(Node[] parseNodes, @NonNull ExpressionNode leftExpressionNode, @NonNull BinaryOpType binaryOpType, @NonNull ExpressionNode rightExpressionNode) {
		super(parseNodes);
		this.leftExpressionNode = leftExpressionNode;
		this.binaryOpType = binaryOpType;
		this.rightExpressionNode = rightExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		leftExpressionNode.setScopes(this);
		rightExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		leftExpressionNode.defineTypes(this);
		rightExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		leftExpressionNode.declareExpressions(this);
		rightExpressionNode.declareExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		leftExpressionNode.checkTypes(this);
		rightExpressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		leftExpressionNode.foldConstants(this);
		rightExpressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode leftConstantExpressionNode = leftExpressionNode.constantExpressionNode();
		if (leftConstantExpressionNode != null) {
			leftExpressionNode = leftConstantExpressionNode;
		}
		
		@Nullable ConstantExpressionNode rightConstantExpressionNode = rightExpressionNode.constantExpressionNode();
		if (rightConstantExpressionNode != null) {
			rightExpressionNode = rightConstantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		leftExpressionNode.trackFunctions(this);
		rightExpressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		leftExpressionNode.generateIntermediate(this);
		rightExpressionNode.generateIntermediate(this);
		routine.addBinaryOpAction(this, leftExpressionNode.getTypeInfo(), binaryOpType, rightExpressionNode.getTypeInfo(), dataId = routine.nextRegId(typeInfo), leftExpressionNode.dataId, rightExpressionNode.dataId);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = Main.generator.binaryOpTypeInfo(this, leftExpressionNode.getTypeInfo(), binaryOpType, rightExpressionNode.getTypeInfo());
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		@Nullable Value leftConstantValue = leftExpressionNode.getConstantValue();
		if (leftConstantValue != null) {
			@Nullable Value rightConstantValue = rightExpressionNode.getConstantValue();
			if (rightConstantValue != null) {
				constantValue = Main.generator.binaryOp(this, leftConstantValue, binaryOpType, rightConstantValue);
				return;
			}
		}
		constantValue = null;
	}
}
