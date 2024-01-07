package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class AssignmentExpressionNode extends ExpressionNode {
	
	public final @NonNull ExpressionNode lvalueExpressionNode;
	public final @NonNull AssignmentOpType assignmentOpType;
	public @NonNull ExpressionNode rvalueExpressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public AssignmentExpressionNode(Node[] parseNodes, @NonNull ExpressionNode lvalueExpressionNode, @NonNull AssignmentOpType assignmentOpType, @NonNull ExpressionNode rvalueExpressionNode) {
		super(parseNodes);
		this.lvalueExpressionNode = lvalueExpressionNode;
		this.assignmentOpType = assignmentOpType;
		this.rvalueExpressionNode = rvalueExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = new Scope(parent.scope);
		
		rvalueExpressionNode.setScopes(this);
		lvalueExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		rvalueExpressionNode.defineTypes(this);
		lvalueExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		rvalueExpressionNode.declareExpressions(this);
		lvalueExpressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		rvalueExpressionNode.defineExpressions(this);
		
		lvalueExpressionNode.setIsLvalue();
		
		lvalueExpressionNode.defineExpressions(this);
		
		setTypeInfo();
		
		if (!lvalueExpressionNode.isValidLvalue()) {
			throw error("Attempted to assign to invalid lvalue expression!");
		}
		lvalueExpressionNode.checkIsAssignable(this);
		lvalueExpressionNode.initialize(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		rvalueExpressionNode.checkTypes(this);
		lvalueExpressionNode.checkTypes(this);
		
		@NonNull TypeInfo rvalueType = rvalueExpressionNode.getTypeInfo(), lvalueType = lvalueExpressionNode.getTypeInfo();
		@Nullable BinaryOpType binaryOpType = assignmentOpType.binaryOpType;
		if (binaryOpType != null) {
			rvalueType = Main.generator.binaryOpTypeInfo(this, lvalueType, binaryOpType, rvalueType);
		}
		if (!rvalueType.canImplicitCastTo(lvalueType)) {
			throw castError("assignment value", rvalueType, lvalueType);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		rvalueExpressionNode.foldConstants(this);
		lvalueExpressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantRvalueExpressionNode = rvalueExpressionNode.constantExpressionNode();
		if (constantRvalueExpressionNode != null) {
			rvalueExpressionNode = constantRvalueExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		rvalueExpressionNode.trackFunctions(this);
		lvalueExpressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		rvalueExpressionNode.generateIntermediate(this);
		lvalueExpressionNode.generateIntermediate(this);
		
		@Nullable BinaryOpType binaryOpType = assignmentOpType.binaryOpType;
		@NonNull TypeInfo lvalueType = lvalueExpressionNode.getTypeInfo();
		DataId temp = lvalueExpressionNode.dataId.addDereference(this);
		if (binaryOpType != null) {
			DataId original = routine.nextRegId(lvalueType);
			routine.addAssignmentAction(this, original, temp);
			routine.addBinaryOpAction(this, lvalueType, binaryOpType, rvalueExpressionNode.getTypeInfo(), temp, original, rvalueExpressionNode.dataId);
		}
		else {
			routine.addAssignmentAction(this, temp, rvalueExpressionNode.dataId);
		}
		routine.addAssignmentAction(this, dataId = routine.nextRegId(lvalueType), temp);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = lvalueExpressionNode.getTypeInfo();
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
}
