package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.Value;
import drlc.node.Node;

public class WordExpressionNode extends ExpressionNode {
	
	public final long value;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	public @Nullable Value<?> constantValue = null;
	
	public WordExpressionNode(Node[] parseNodes, long value) {
		super(parseNodes);
		this.value = value;
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		setTypeInfo(null);
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		
	}
	
	@Override
	public void trackFunctions(ASTNode<?> parent) {
		
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
		if (Main.generator.intTypeInfo.equals(typeInfo)) {
			constantValue = Main.generator.intValue(value);
		}
		else if (Main.generator.natTypeInfo.equals(typeInfo)) {
			constantValue = Main.generator.natValue(value);
		}
		else {
			throw error("Could not infer type of constant value!");
		}
	}
}
