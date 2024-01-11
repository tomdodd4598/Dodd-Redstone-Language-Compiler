package drlc.intermediate.scope;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;

public class FunctionScope extends Scope {
	
	@SuppressWarnings("null")
	public @NonNull Function function = null;
	
	public FunctionScope(ASTNode<?> node, Scope parent) {
		super(node, parent);
		definiteExecution = false;
		potentialOuterMultipleExecution = true;
	}
	
	@Override
	public @NonNull FunctionScope getContextFunctionScope() {
		return this;
	}
	
	@Override
	public @NonNull Function getContextFunction() {
		return function;
	}
}
