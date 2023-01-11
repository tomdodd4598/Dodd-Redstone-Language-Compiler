package drlc.intermediate.routine;

import drlc.*;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.expression.ExpressionInfo;
import drlc.intermediate.component.info.DeclaratorInfo;
import drlc.intermediate.component.type.*;
import drlc.node.Node;

public class FunctionRoutine extends Routine {
	
	public final Function function;
	
	public FunctionRoutine(Node node, Generator generator, String name, Function function) {
		super(generator, name);
		this.function = function;
		if (function.returnTypeInfo.isVoid(node)) {
			getDestructionActionList().add(Global.RETURN_FROM_FUNCTION);
		}
	}
	
	@Override
	public RoutineType getType() {
		return type;
	}
	
	@Override
	public void onRequiresNesting() {
		type = type.onNesting();
	}
	
	@Override
	public void onRequiresStack() {
		type = type.onRecursion();
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
	public void setLastExpressionInfo(Node node, ExpressionInfo expressionInfo) {
		TypeInfo expressionTypeInfo = expressionInfo.getTypeInfo();
		if (expressionTypeInfo.isFunction()) {
			Function function = ((FunctionTypeInfo) expressionTypeInfo).function;
			if (function != null && !expressionInfo.isDirectFunction) {
				onRequiresStack();
			}
		}
		super.setLastExpressionInfo(node, expressionInfo);
	}
	
	@Override
	public String toString() {
		return function.toString();
	}
}
