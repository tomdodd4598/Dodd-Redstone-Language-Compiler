package drlc.intermediate.ast.element;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public class DeclaratorNode extends ASTNode<Scope> {
	
	public @NonNull VariableModifier variableModifier;
	public final @NonNull String name;
	public final @Nullable TypeNode typeNode;
	
	@SuppressWarnings("null")
	public @NonNull DeclaratorInfo declaratorInfo = null;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo inferredTypeInfo = null;
	
	public boolean functionParameter = false;
	
	public DeclaratorNode(Source source, @NonNull VariableModifier variableModifier, @NonNull String name, @Nullable TypeNode typeNode) {
		super(source);
		this.variableModifier = variableModifier;
		this.name = name;
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		scope.addConstantShadow(name);
		
		if (typeNode != null) {
			typeNode.setScopes(this);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		if (typeNode != null) {
			typeNode.defineTypes(this);
		}
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		if (typeNode != null) {
			typeNode.declareExpressions(this);
		}
		
		if (!functionParameter && routine.isRootRoutine()) {
			variableModifier = new VariableModifier(true, variableModifier.mutable);
		}
		
		if (functionParameter) {
			declaratorInfo = new DeclaratorInfo(new Variable(name, variableModifier, typeNode.getTypeInfo()));
			scope.addVariable(this, declaratorInfo.variable);
			scope.onVariableInitialization(this, declaratorInfo.variable);
		}
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		if (typeNode != null) {
			typeNode.defineExpressions(this);
		}
		
		if (!functionParameter) {
			declaratorInfo = new DeclaratorInfo(new Variable(name, variableModifier, typeNode == null ? inferredTypeInfo : typeNode.getTypeInfo()));
			scope.addVariable(this, declaratorInfo.variable);
		}
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		if (typeNode != null) {
			typeNode.checkTypes(this);
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		if (typeNode != null) {
			typeNode.foldConstants(this);
		}
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		if (typeNode != null) {
			typeNode.trackFunctions(this);
		}
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		if (typeNode != null) {
			typeNode.generateIntermediate(this);
		}
		
		if (!functionParameter) {
			routine.declaratorList.add(declaratorInfo);
		}
	}
}
