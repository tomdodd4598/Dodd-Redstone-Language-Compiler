package drlc.intermediate.ast.expression;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.component.value.*;

public class CharExpressionNode extends ConstantExpressionNode {
	
	public final @NonNull CharValue value;
	
	public CharExpressionNode(Source source, char value) {
		super(source);
		this.value = Main.generator.charValue((byte) value);
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
		routine.addValueAssignmentAction(this, dataId = routine.nextRegId(Main.generator.charTypeInfo), value);
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return Main.generator.charTypeInfo;
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
