package drlc.intermediate.ast.section;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.ast.expression.*;
import drlc.intermediate.ast.pattern.PatternNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public class VariableDeclarationNode extends StaticSectionNode<Scope> {
	
	public final @NonNull PatternNode patternNode;
	public @Nullable ExpressionNode expressionNode;
	
	public VariableDeclarationNode(Source source, @NonNull PatternNode patternNode, @Nullable ExpressionNode expressionNode) {
		super(source);
		this.patternNode = patternNode;
		this.expressionNode = expressionNode;
		
		patternNode.checkDeclaratorNames();
		
		if (expressionNode == null) {
			if (isStaticVariable()) {
				throw error("Static variables require an initializer!");
			}
			if (!patternNode.canDeclareExcludingInitializer()) {
				throw error("Can not infer type without variable initializer!");
			}
		}
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		if (expressionNode != null) {
			expressionNode.setScopes(this);
		}
		patternNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.defineTypes(this);
		}
		patternNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = isStaticVariable() ? Main.rootRoutine : parent.routine;
		
		if (expressionNode != null) {
			expressionNode.declareExpressions(this);
		}
		patternNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.setTypeInfo(patternNode.getExplicitTypeInfo());
			expressionNode.defineExpressions(this);
			
			patternNode.setTypeInfo(expressionNode.getTypeInfo());
			patternNode.defineExpressions(this);
		}
		else {
			if (!patternNode.canDeclareExcludingInitializer()) {
				throw error("Can not infer type without variable initializer!");
			}
			
			TypeInfo typeInfo = patternNode.getExplicitTypeInfo();
			if (typeInfo == null) {
				throw error("Could not infer type of variable!");
			}
			
			patternNode.setTypeInfo(typeInfo);
			patternNode.defineExpressions(this);
		}
		
		for (DeclaratorNode declaratorNode : patternNode.getDeclaratorNodes()) {
			if (expressionNode != null) {
				scope.onVariableInitialization(this, declaratorNode.declaratorInfo.variable);
			}
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.checkTypes(this);
		}
		patternNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.foldConstants(this);
		}
		patternNode.foldConstants(this);
		
		if (expressionNode != null) {
			@Nullable ConstantExpressionNode constantExpressionNode = expressionNode.constantExpressionNode();
			if (constantExpressionNode != null) {
				expressionNode = constantExpressionNode;
			}
		}
		
		if (expressionNode != null && isStaticVariable() && !expressionNode.isStatic()) {
			throw error("Static variables require a static initializer!");
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		if (expressionNode != null) {
			expressionNode.generateIntermediate(this);
		}
		
		patternNode.dataId = expressionNode == null ? null : expressionNode.dataId;
		patternNode.generateIntermediate(this);
	}
	
	protected boolean isStaticVariable() {
		return patternNode.hasStaticBinding();
	}
}
