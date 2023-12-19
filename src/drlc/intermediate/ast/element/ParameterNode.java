package drlc.intermediate.ast.element;

import org.eclipse.jdt.annotation.*;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.*;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.Scope;
import drlc.node.Node;

public class ParameterNode extends ASTNode<Scope, Routine> {
	
	public final @NonNull VariableModifier variableModifier;
	public final @Nullable String name;
	public final @NonNull TypeNode typeNode;
	
	public int index;
	
	@SuppressWarnings("null")
	public @NonNull DeclaratorInfo declaratorInfo = null;
	
	public ParameterNode(Node[] parseNodes, @NonNull VariableModifier variableModifier, @Nullable String name, @NonNull TypeNode typeNode) {
		super(parseNodes);
		this.variableModifier = variableModifier;
		this.name = name;
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		typeNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		typeNode.declareExpressions(this);
		
		declaratorInfo = new DeclaratorInfo(this, new Variable(getVariableName(), variableModifier, typeNode.typeInfo));
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		typeNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		typeNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		typeNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		typeNode.generateIntermediate(this);
	}
	
	public @NonNull String getVariableName() {
		return name != null ? name : Global.DISCARD_PARAM_PREFIX + index;
	}
}
