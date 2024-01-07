package drlc.intermediate.routine;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.action.ReturnAction;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.TypeInfo;

public class FunctionRoutine extends Routine {
	
	public final Function function;
	
	public FunctionRoutine(ASTNode<?, ?> node, Function function) {
		super(function.name);
		this.function = function;
		if (function.returnTypeInfo.equals(Main.generator.voidTypeInfo)) {
			getDestructionActionList().add(new ReturnAction(null, Main.generator.unitValue.dataId()));
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
	public int hashCode() {
		return Objects.hash(name, scope, function);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionRoutine) {
			FunctionRoutine other = (FunctionRoutine) obj;
			return super.equals(obj) && function.equals(other.function);
		}
		return false;
	}
	
	@Override
	public String toString() {
		return function.routineString();
	}
}
