package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.*;
import drlc.node.Node;

public class SizeofExpressionNode extends ConstantExpressionNode {
	
	public final @NonNull TypeNode typeNode;
	
	@SuppressWarnings("null")
	public @NonNull IntValue value = null;
	
	public SizeofExpressionNode(Node[] parseNodes, @NonNull TypeNode typeNode) {
		super(parseNodes);
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
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		typeNode.defineExpressions(this);
		
		setConstantValue();
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
		routine.addValueAssignmentAction(this, dataId = routine.nextRegId(Main.generator.indexTypeInfo), value);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return Main.generator.indexTypeInfo;
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
		typeNode.setTypeInfo();
		
		value = Main.generator.indexValue(typeNode.typeInfo.getSize());
	}
}
