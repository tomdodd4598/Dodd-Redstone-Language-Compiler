package drlc.intermediate.routine;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;

public class FunctionRoutine extends Routine {
	
	public final Function function;
	public boolean isDefined = false;
	
	public FunctionRoutine(ASTNode<?, ?> node, Function function) {
		super(function.name);
		this.function = function;
		if (function.returnTypeInfo.isVoid()) {
			getDestructionActionList().add(Global.RETURN_FROM_FUNCTION);
		}
	}
	
	@Override
	public RoutineCallType getType() {
		return type;
	}
	
	@Override
	public void onRequiresNesting() {
		type = type.onRequiresNesting();
	}
	
	@Override
	public void onRequiresStack() {
		type = type.onRequiresRecursion();
	}
	
	@Override
	public boolean isFunctionRoutine() {
		return true;
	}
	
	@Override
	public boolean isBuiltInFunctionRoutine() {
		return function.builtIn;
	}
	
	@Override
	public boolean isDefined() {
		return isDefined;
	}
	
	@Override
	public Function getFunction() {
		return function;
	}
	
	@Override
	public @NonNull TypeInfo getReturnTypeInfo() {
		return function.returnTypeInfo;
	}
	
	@Override
	public List<DeclaratorInfo> getParams() {
		return function.params;
	}
	
	@Override
	public void onNonLocalFunctionItemExpression(ASTNode<?, ?> node, Function function) {
		onRequiresStack();
		super.onNonLocalFunctionItemExpression(node, function);
	}
	
	@Override
	public String toString() {
		return function.toString();
	}
}
