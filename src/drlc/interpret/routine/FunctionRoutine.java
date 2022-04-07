package drlc.interpret.routine;

import drlc.Global;
import drlc.interpret.Program;
import drlc.interpret.component.Function;
import drlc.interpret.component.info.DeclaratorInfo;
import drlc.interpret.component.info.expression.ExpressionInfo;
import drlc.interpret.component.info.type.*;
import drlc.node.Node;

public class FunctionRoutine extends Routine {
	
	public final Function function;
	
	public FunctionRoutine(Node node, Program program, String name, Function function) {
		super(program, name);
		this.function = function;
		if (function.returnTypeInfo.isVoid(node, program.generator)) {
			getDestructionActionList().add(Global.RETURN_FROM_FUNCTION);
		}
	}
	
	@Override
	public RoutineType getType() {
		if (function.modifierInfo.stack_) {
			return RoutineType.STACK;
		}
		else if (function.modifierInfo.static_) {
			return baseType.equals(RoutineType.LEAF) ? RoutineType.LEAF : RoutineType.NESTING;
		}
		else {
			return baseType;
		}
	}
	
	@Override
	public void onRequiresNesting(boolean force) {
		baseType = baseType.onNesting();
		if (force) {
			function.modifierInfo.static_ = true;
		}
	}
	
	@Override
	public void onRequiresStack(boolean force) {
		baseType = baseType.onRecursion();
		if (force) {
			function.modifierInfo.stack_ = true;
		}
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
	public TypeInfo getReturnTypeInfo() {
		return function.returnTypeInfo;
	}
	
	@Override
	public DeclaratorInfo[] getParams() {
		return function.params;
	}
	
	@Override
	public void setLastExpressionInfo(Node node, ExpressionInfo<?> expressionInfo) {
		TypeInfo expressionTypeInfo = expressionInfo.getTypeInfo();
		if (expressionTypeInfo.isFunction()) {
			Function function = ((FunctionTypeInfo) expressionTypeInfo).function;
			if (function != null && !expressionInfo.isDirectFunction) {
				onRequiresStack(true);
			}
		}
		super.setLastExpressionInfo(node, expressionInfo);
	}
	
	@Override
	public String toString() {
		return function.toString();
	}
}
