package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class NullExpressionNode extends ConstantExpressionNode {
	
	public NullExpressionNode(Node[] parseNodes) {
		super(parseNodes);
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
		routine.incrementRegId(Main.generator.wildcardPtrTypeInfo);
		routine.addImmediateRegisterAssignmentAction(this, Main.generator.nullValue);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return Main.generator.wildcardPtrTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		
	}
	
	@Override
	protected @NonNull Value getConstantValueInternal() {
		return Main.generator.nullValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
}
