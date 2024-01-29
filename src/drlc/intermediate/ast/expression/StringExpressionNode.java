package drlc.intermediate.ast.expression;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;

public class StringExpressionNode extends ExpressionNode {
	
	public final String literal;
	
	@SuppressWarnings("null")
	public @NonNull TypeInfo typeInfo = null;
	
	@SuppressWarnings("null")
	public @NonNull ArrayValue stringValue = null;
	
	public StringExpressionNode(Source source, String literal) {
		super(source);
		this.literal = literal;
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
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		@NonNull DataId arrayDataId = scope.nextLocalDataId(routine, stringValue.typeInfo);
		routine.addValueAssignmentAction(this, arrayDataId, stringValue);
		
		@NonNull DataId ptrDataId = routine.nextRegId(Main.generator.charTypeInfo(false));
		routine.addAddressAssignmentAction(this, ptrDataId, arrayDataId);
		
		@NonNull DataId lenDataId = routine.nextRegId(Main.generator.natTypeInfo);
		routine.addValueAssignmentAction(this, lenDataId, Main.generator.natValue(literal.length()));
		
		routine.addCompoundAssignmentAction(this, dataId = routine.nextRegId(typeInfo), Helpers.arrayList(ptrDataId, lenDataId));
	}
	
	@Override
	protected @NonNull TypeInfo getTypeInfoInternal() {
		return typeInfo;
	}
	
	@Override
	protected void setTypeInfoInternal(@Nullable TypeInfo targetType) {
		typeInfo = Main.rootScope.getTypeInfo(this, Global.CHARS, false);
		
		stringValue = new ArrayValue(this, new ArrayTypeInfo(this, new ArrayList<>(), Main.generator.charTypeInfo, literal.length()), literal.chars().mapToObj(x -> Main.generator.charValue((byte) x)).collect(Collectors.toList()));
	}
	
	@Override
	protected @Nullable Value<?> getConstantValueInternal() {
		return null;
	}
	
	@Override
	protected void setConstantValueInternal() {
		
	}
}
