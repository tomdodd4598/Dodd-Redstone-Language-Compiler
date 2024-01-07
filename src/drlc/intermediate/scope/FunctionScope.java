package drlc.intermediate.scope;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.component.Function;

public class FunctionScope extends Scope {
	
	@SuppressWarnings("null")
	public @NonNull Function function = null;
	
	public FunctionScope(Scope parent) {
		super(parent);
		definiteExecution = false;
		potentialOuterMultipleExecution = true;
	}
	
	@Override
	public @Nullable FunctionScope getFunctionScope() {
		return this;
	}
	
	@Override
	public @NonNull Function getContextFunction() {
		return function;
	}
}
