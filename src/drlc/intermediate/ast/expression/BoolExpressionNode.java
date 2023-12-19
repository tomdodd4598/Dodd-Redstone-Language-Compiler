package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class BoolExpressionNode extends ConstantExpressionNode {
	
	public final @NonNull BoolValue value;
	
	public BoolExpressionNode(Node[] parseNodes, boolean value) {
		super(parseNodes);
		this.value = Main.generator.boolValue(value);
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		routine.incrementRegId(Main.generator.boolTypeInfo);
		routine.addImmediateRegisterAssignmentAction(this, value);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return Main.generator.boolTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		
	}
	
	@Override
	protected @NonNull Value getConstantValueInternal() {
		return value;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
}
