package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class AssignmentStatementNode extends BasicSectionNode<Scope, Routine> {
	
	public final @NonNull ExpressionNode lvalueExpressionNode;
	public final @NonNull AssignmentOpType assignmentOpType;
	public @NonNull ExpressionNode rvalueExpressionNode;
	
	public AssignmentStatementNode(Node[] parseNodes, @NonNull ExpressionNode lvalueExpressionNode, @NonNull AssignmentOpType assignmentOpType, @NonNull ExpressionNode rvalueExpressionNode) {
		super(parseNodes);
		this.lvalueExpressionNode = lvalueExpressionNode;
		this.assignmentOpType = assignmentOpType;
		this.rvalueExpressionNode = rvalueExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
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
	public void checkTypes(ASTNode<?, ?> parent) {
		rvalueExpressionNode.checkTypes(this);
		lvalueExpressionNode.checkTypes(this);
		
		if (!lvalueExpressionNode.isValidLvalue()) {
			throw error("Attempted to assign to invalid lvalue expression!");
		}
		lvalueExpressionNode.setIsLvalue();
		
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
		if (binaryOpType != null) {
			routine.addLvalueAssignmentOpAction(this, lvalueExpressionNode.getTypeInfo(), binaryOpType, rvalueExpressionNode.getTypeInfo(), lvalueExpressionNode.dataId, rvalueExpressionNode.dataId);
		}
		else {
			routine.addLvalueAssignmentAction(this, lvalueExpressionNode.dataId, rvalueExpressionNode.dataId);
		}
	}
}
