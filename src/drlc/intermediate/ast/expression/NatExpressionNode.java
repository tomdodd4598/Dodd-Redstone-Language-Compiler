package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.*;

public class NatExpressionNode extends ConstantExpressionNode {
	
	public final @NonNull NatValue value;
	
	public NatExpressionNode(Source source, long value) {
		super(source);
		this.value = Main.generator.natValue(value);
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
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		routine.addValueAssignmentAction(this, dataId = routine.nextRegId(Main.generator.natTypeInfo), value);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return Main.generator.natTypeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		
	}
	
	@Override
	protected @NonNull Value<?> getConstantValueInternal() {
		return value;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
}
