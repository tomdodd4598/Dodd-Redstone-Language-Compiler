package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.IterativeScope;

public class ConditionalIterativeSectionNode extends IterativeSectionNode {
	
	public final boolean _do, until;
	public @NonNull ExpressionNode expressionNode;
	public final @NonNull ScopedBodyNode bodyNode;
	
	public ConditionalIterativeSectionNode(Source source, @Nullable String label, boolean _do, boolean until, @NonNull ExpressionNode expressionNode, @NonNull ScopedBodyNode bodyNode) {
		super(source, label);
		this._do = _do;
		this.until = until;
		this.expressionNode = expressionNode;
		this.bodyNode = bodyNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new IterativeScope(this, null, parent.scope, false, _do, label);
		
		expressionNode.setScopes(this);
		bodyNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		expressionNode.defineTypes(this);
		bodyNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		bodyNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		expressionNode.setTypeInfo(Main.generator.boolTypeInfo);
		
		expressionNode.defineExpressions(this);
		bodyNode.defineExpressions(this);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		expressionNode.checkTypes(this);
		bodyNode.checkTypes(this);
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.canImplicitCastTo(Main.generator.boolTypeInfo)) {
			throw castError("condition value", expressionType, Main.generator.boolTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		expressionNode.foldConstants(this);
		bodyNode.foldConstants(this);
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		expressionNode.trackFunctions(this);
		bodyNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		if (!_do) {
			routine.addAction(scope.continueJump);
		}
		routine.incrementSectionId();
		int cjTarget = routine.currentSectionId();
		bodyNode.generateIntermediate(this);
		
		routine.incrementSectionId();
		scope.continueJump.setTarget(routine.currentSectionId());
		expressionNode.generateIntermediate(this);
		routine.addConditionalJumpAction(this, cjTarget, !until);
		
		routine.incrementSectionId();
		scope.breakJump.setTarget(routine.currentSectionId());
	}
}
