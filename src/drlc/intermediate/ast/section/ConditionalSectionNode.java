package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.ConditionalScope;

public class ConditionalSectionNode extends RuntimeSectionNode<ConditionalScope> {
	
	public final boolean unless;
	public @NonNull ExpressionNode expressionNode;
	public final @NonNull ScopedBodyNode thenNode;
	public final @Nullable ASTNode<?> elseNode;
	
	public ConditionalSectionNode(Source source, boolean unless, @NonNull ExpressionNode expressionNode, @NonNull ScopedBodyNode thenNode, @Nullable ASTNode<?> elseNode) {
		super(source);
		this.unless = unless;
		this.expressionNode = expressionNode;
		this.thenNode = thenNode;
		this.elseNode = elseNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new ConditionalScope(this, null, parent.scope, false, elseNode != null);
		
		expressionNode.setScopes(this);
		thenNode.setScopes(this);
		if (elseNode != null) {
			elseNode.setScopes(this);
		}
		
		thenNode.scope.definiteExecution = false;
		if (elseNode != null) {
			elseNode.scope.definiteExecution = false;
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		expressionNode.defineTypes(this);
		thenNode.defineTypes(this);
		if (elseNode != null) {
			elseNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
		thenNode.declareExpressions(this);
		if (elseNode != null) {
			elseNode.declareExpressions(this);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		expressionNode.defineExpressions(this);
		thenNode.defineExpressions(this);
		if (elseNode != null) {
			elseNode.defineExpressions(this);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		expressionNode.checkTypes(this);
		thenNode.checkTypes(this);
		if (elseNode != null) {
			elseNode.checkTypes(this);
		}
		
		@NonNull TypeInfo expressionType = expressionNode.getTypeInfo();
		if (!expressionType.canImplicitCastTo(Main.generator.boolTypeInfo)) {
			throw castError("condition value", expressionType, Main.generator.boolTypeInfo);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		expressionNode.foldConstants(this);
		thenNode.foldConstants(this);
		if (elseNode != null) {
			elseNode.foldConstants(this);
		}
		
		@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
		if (constantExpressionNode != null) {
			expressionNode = constantExpressionNode;
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		expressionNode.trackFunctions(this);
		thenNode.trackFunctions(this);
		if (elseNode != null) {
			elseNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		expressionNode.generateIntermediate(this);
		ConditionalJumpAction cja = routine.addConditionalJumpAction(this, -1, unless);
		thenNode.generateIntermediate(this);
		
		if (elseNode != null) {
			JumpAction ja = routine.addJumpAction(this, -1);
			
			routine.incrementSectionId();
			cja.setTarget(routine.currentSectionId());
			elseNode.generateIntermediate(this);
			
			routine.incrementSectionId();
			ja.setTarget(routine.currentSectionId());
		}
		else {
			routine.incrementSectionId();
			cja.setTarget(routine.currentSectionId());
		}
	}
}
