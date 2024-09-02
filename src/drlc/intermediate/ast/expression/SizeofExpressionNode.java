package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.type.TypeNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;

public class SizeofExpressionNode extends ExpressionNode {
	
	public final @NonNull TypeNode typeNode;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public @Nullable Value<?> constantValue = null;
	
	public SizeofExpressionNode(Source source, @NonNull TypeNode typeNode) {
		super(source);
		this.typeNode = typeNode;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		typeNode.setScopes(this);
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		typeNode.defineTypes(this);
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
		
		typeNode.declareExpressions(this);
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		typeNode.defineExpressions(this);
		
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		typeNode.checkTypes(this);
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		typeNode.foldConstants(this);
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		typeNode.trackFunctions(this);
	}
	
	@SuppressWarnings("null")
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		routine.addValueAssignmentAction(this, dataId = routine.nextRegId(typeInfo), getConstantValue());
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		if (targetType == null || Main.generator.intTypeInfo.equals(targetType)) {
			typeInfo = Main.generator.intTypeInfo;
		}
		else if (Main.generator.natTypeInfo.equals(targetType)) {
			typeInfo = Main.generator.natTypeInfo;
		}
		else if (Main.generator.charTypeInfo.equals(targetType)) {
			typeInfo = Main.generator.charTypeInfo;
		}
		else {
			throw error("Could not infer type of constant value!");
		}
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return constantValue;
	}
	
	@Override
	protected void setConstantValueInternal() {
		typeNode.setTypeInfo();
		
		int size = typeNode.getTypeInfo().getSize();
		if (Main.generator.intTypeInfo.equals(typeInfo)) {
			constantValue = Main.generator.intValue(size);
		}
		else if (Main.generator.natTypeInfo.equals(typeInfo)) {
			constantValue = Main.generator.natValue(size);
		}
		else if (Main.generator.charTypeInfo.equals(typeInfo)) {
			constantValue = Main.generator.charValue(size);
		}
		else {
			throw error("Could not infer type of constant value!");
		}
	}
	
	@Override
	public boolean isStatic() {
		return true;
	}
}
