package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class AddressExpressionNode extends ExpressionNode {
	
	public final boolean mutable;
	public final @NonNull ExpressionNode expressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public AddressExpressionNode(Node[] parseNodes, boolean mutable, @NonNull ExpressionNode expressionNode) {
		super(parseNodes);
		this.mutable = mutable;
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?, ?> parent) {
		scope = parent.scope;
		
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?, ?> parent) {
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?, ?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?, ?> parent) {
		expressionNode.defineExpressions(this);
		
		setTypeInfo();
	}
	
	@Override
	public void checkTypes(ASTNode<?, ?> parent) {
		expressionNode.checkTypes(this);
		
		if (expressionNode.isValidLvalue()) {
			expressionNode.setIsLvalue();
			
			if (mutable && !expressionNode.isMutableLvalue()) {
				throw error("Attempted to create mutable reference of immutable lvalue expression!");
			}
		}
	}
	
	@Override
	public void foldConstants(ASTNode<?, ?> parent) {
		expressionNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?, ?> parent) {
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?, ?> parent) {
		expressionNode.generateIntermediate(this);
		
		if (expressionNode.getIsLvalue()) {
			dataId = expressionNode.dataId;
		}
		else {
			DataId temp = scope.nextLocalDataId(routine, expressionNode.getTypeInfo());
			routine.addAssignmentAction(this, temp, expressionNode.dataId);
			routine.addAddressAssignmentAction(this, dataId = routine.nextRegId(typeInfo), temp);
		}
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal() {
		typeInfo = expressionNode.getTypeInfo().addressOf(this, mutable);
	}
	
	@Override
	protected @Nullable Value getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
	
	@Override
	public @Nullable Function getDirectFunction() {
		return expressionNode.getDirectFunction();
	}
}
