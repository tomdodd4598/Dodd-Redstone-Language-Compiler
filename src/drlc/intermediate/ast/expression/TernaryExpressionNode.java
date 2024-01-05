package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.ConditionalScope;
import drlc.node.Node;

public class TernaryExpressionNode extends ExpressionNode {
	
	public @NonNull ExpressionNode conditionExpressionNode;
	public @NonNull ExpressionNode trueExpressionNode;
	public @NonNull ExpressionNode falseExpressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public @Nullable Value constantValue = null;
	
	public TernaryExpressionNode(Node[] parseNodes, @NonNull ExpressionNode conditionExpressionNode, @NonNull ExpressionNode trueExpressionNode, @NonNull ExpressionNode falseExpressionNode) {
		super(parseNodes);
		this.conditionExpressionNode = conditionExpressionNode;
		this.trueExpressionNode = trueExpressionNode;
		this.falseExpressionNode = falseExpressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		@NonNull ConditionalScope conditionalScope = new ConditionalScope(parent.scope, true);
		scope = conditionalScope;
		
		conditionExpressionNode.setScopes(this);
		trueExpressionNode.setScopes(this);
		falseExpressionNode.setScopes(this);
		
		trueExpressionNode.scope.definiteExecution = false;
		falseExpressionNode.scope.definiteExecution = false;
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		conditionExpressionNode.defineTypes(this);
		trueExpressionNode.defineTypes(this);
		falseExpressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		conditionExpressionNode.declareExpressions(this);
		trueExpressionNode.declareExpressions(this);
		falseExpressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		conditionExpressionNode.defineExpressions(this);
		trueExpressionNode.defineExpressions(this);
		falseExpressionNode.defineExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		conditionExpressionNode.checkTypes(this);
		trueExpressionNode.checkTypes(this);
		falseExpressionNode.checkTypes(this);
		
		@NonNull TypeInfo conditionExpressionType = conditionExpressionNode.getTypeInfo();
		if (!conditionExpressionType.canImplicitCastTo(Main.generator.boolTypeInfo)) {
			throw castError("conditional value", conditionExpressionType, Main.generator.boolTypeInfo);
		}
		
		@NonNull TypeInfo trueExpressionType = trueExpressionNode.getTypeInfo(), falseExpressionType = falseExpressionNode.getTypeInfo();
		if (!trueExpressionType.equals(falseExpressionType)) {
			throw error("Ternary expression branches have unequal types \"%s\" and \"%s\"!", trueExpressionType, falseExpressionType);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		conditionExpressionNode.foldConstants(this);
		trueExpressionNode.foldConstants(this);
		falseExpressionNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode conditionConstantExpressionNode = conditionExpressionNode.constantExpressionNode();
		if (conditionConstantExpressionNode != null) {
			conditionExpressionNode = conditionConstantExpressionNode;
		}
		
		@Nullable ConstantExpressionNode trueConstantExpressionNode = trueExpressionNode.constantExpressionNode();
		if (trueConstantExpressionNode != null) {
			trueExpressionNode = trueConstantExpressionNode;
		}
		
		@Nullable ConstantExpressionNode falseConstantExpressionNode = falseExpressionNode.constantExpressionNode();
		if (falseConstantExpressionNode != null) {
			falseExpressionNode = falseConstantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		conditionExpressionNode.trackFunctions(this);
		trueExpressionNode.trackFunctions(this);
		falseExpressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		DataId temp = scope.nextLocalDataId(routine, typeInfo);
		conditionExpressionNode.generateIntermediate(this);
		routine.addAssignmentAction(this, routine.nextRegId(Main.generator.boolTypeInfo), conditionExpressionNode.dataId);
		ConditionalJumpAction cja = routine.addConditionalJumpAction(this, -1, false);
		
		trueExpressionNode.generateIntermediate(this);
		routine.addAssignmentAction(this, temp, trueExpressionNode.dataId);
		JumpAction ja = routine.addJumpAction(this, -1);
		
		routine.incrementSectionId();
		cja.setTarget(routine.currentSectionId());
		falseExpressionNode.generateIntermediate(this);
		routine.addAssignmentAction(this, temp, falseExpressionNode.dataId);
		
		routine.incrementSectionId();
		ja.setTarget(routine.currentSectionId());
		routine.addAssignmentAction(this, dataId = routine.nextRegId(typeInfo), temp);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = trueExpressionNode.getTypeInfo();
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		@Nullable Value conditionConstantValue = conditionExpressionNode.getConstantValue();
		if (Main.generator.trueValue.equals(conditionConstantValue)) {
			constantValue = trueExpressionNode.getConstantValue();
		}
		else if (Main.generator.falseValue.equals(conditionConstantValue)) {
			constantValue = falseExpressionNode.getConstantValue();
		}
		else {
			constantValue = null;
		}
	}
}
