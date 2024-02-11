package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class DereferenceExpressionNode extends ExpressionNode {
	
	public final @NonNull ExpressionNode expressionNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public boolean isLvalue = false;
	
	public DereferenceExpressionNode(Source source, @NonNull ExpressionNode expressionNode) {
		super(source);
		this.expressionNode = expressionNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = new Scope(this, null, parent.scope, true);
		
		expressionNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		expressionNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		expressionNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		expressionNode.defineExpressions(this);
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		expressionNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		expressionNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		expressionNode.trackFunctions(this);
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		expressionNode.generateIntermediate(this);
		
		if (isLvalue) {
			dataId = expressionNode.dataId;
		}
		else {
			routine.addDereferenceAssignmentAction(this, dataId = routine.nextRegId(typeInfo), expressionNode.dataId);
		}
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		expressionNode.setTypeInfo(targetType == null ? null : targetType.addressOf(this, false));
		typeInfo = expressionNode.getTypeInfo().dereference(this, 1);
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
	
	@Override
	public boolean isValidLvalue() {
		return true;
	}
	
	@Override
	public boolean isMutableLvalue() {
		return expressionNode.isMutableReference();
	}
	
	@Override
	public boolean isMutableReference() {
		return super.isMutableReference() && expressionNode.isMutableReference();
	}
	
	@Override
	public boolean getIsLvalue() {
		return isLvalue;
	}
	
	@Override
	public void setIsLvalue() {
		isLvalue = true;
	}
	
	@Override
	public @Nullable Function getDirectFunction() {
		return expressionNode.getDirectFunction();
	}
}
