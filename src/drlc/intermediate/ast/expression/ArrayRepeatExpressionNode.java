package drlc.intermediate.ast.expression;

import java.util.Collections;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class ArrayRepeatExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode repeatExpressionNode;
	public final @NonNull ExpressionNode constantExpressionNode;
	
	public int length;
	
	@SuppressWarnings("null")
	public @NonNull ArrayTypeInfo typeInfo = null;
	
	public @Nullable ArrayValue constantValue = null;
	
	public ArrayRepeatExpressionNode(Node[] parseNodes, @NonNull ExpressionNode repeatExpressionNode, @NonNull ExpressionNode constantExpressionNode) {
		super(parseNodes);
		this.repeatExpressionNode = repeatExpressionNode;
		this.constantExpressionNode = constantExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		constantExpressionNode.setScopes(this);
		repeatExpressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		@Nullable Value constantValue = constantExpressionNode.getConstantValue();
		if (constantValue != null && constantValue.typeInfo.canImplicitCastTo(Main.generator.indexTypeInfo)) {
			length = constantValue.intValue(this);
			if (length < 0) {
				throw error("Length of array can not be negative!");
			}
		}
		else {
			throw error("Length of array is not a compile-time non-negative \"%s\" constant!", Main.generator.indexTypeInfo);
		}
		
		repeatExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		repeatExpressionNode.declareExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		repeatExpressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		repeatExpressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantRepeatExpressionNode = repeatExpressionNode.constantExpressionNode();
		if (constantRepeatExpressionNode != null) {
			repeatExpressionNode = constantRepeatExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		repeatExpressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		repeatExpressionNode.generateIntermediate(this);
		
		routine.addCompoundAssignmentAction(this, dataId = routine.nextRegId(typeInfo), Collections.nCopies(length, repeatExpressionNode.dataId));
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = new ArrayTypeInfo(this, 0, repeatExpressionNode.getTypeInfo(), length);
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		/*if (length == 0) {
			constantValue = new ArrayValue(this, typeInfo, new ArrayList<>());
			return;
		}
		
		@Nullable Value repeatConstantValue = repeatExpressionNode.getConstantValue();
		if (repeatConstantValue != null) {
			constantValue = new ArrayValue(this, typeInfo, repeatConstantValue, length);
		}
		else {
			constantValue = null;
		}*/
	}
}
