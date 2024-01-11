package drlc.intermediate.ast.expression;

import java.util.ArrayList;

import org.eclipse.jdt.annotation.*;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.section.FunctionDefinitionNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class ClosureExpressionNode extends ExpressionNode {
	
	public final @NonNull String name;
	public final @NonNull FunctionDefinitionNode functionNode;
	
	@SuppressWarnings("null")
	public @NonNull Function function = null;
	
	@SuppressWarnings("null")
	public @NonNull ClosureTypeInfo typeInfo = null;
	
	public ClosureExpressionNode(Node[] parseNodes, @NonNull String name, @NonNull FunctionDefinitionNode functionNode) {
		super(parseNodes);
		this.name = name;
		this.functionNode = functionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		functionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		functionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		functionNode.declareExpressions(this);
		
		function = functionNode.function;
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		functionNode.defineExpressions(this);
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		functionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		functionNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		functionNode.trackFunctions(this);
		
		routine.onNonLocalFunctionItemExpression(this, function);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		functionNode.generateIntermediate(this);
		
		routine.addCompoundAssignmentAction(this, dataId = routine.nextRegId(typeInfo), Helpers.map(function.captures, Variable::dataId));
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		typeInfo = new ClosureTypeInfo(this, new ArrayList<>(), name, function);
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
	
	@Override
	public @Nullable Function getDirectFunction() {
		return function;
	}
}
