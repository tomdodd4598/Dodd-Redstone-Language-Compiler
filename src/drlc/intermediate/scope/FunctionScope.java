package drlc.intermediate.scope;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.component.Function;

public class FunctionScope extends Scope {
	
	@SuppressWarnings("null")
	public @NonNull Function function = null;
	
	public FunctionScope(Scope parent) {
		super(parent);
	}
	
	@Override
	public boolean checkCompleteReturn() {
		return definiteLocalReturn || children.stream().anyMatch(Scope::checkCompleteReturn);
	}
	
	@Override
	public @NonNull Function getContextFunction() {
		return function;
	}
}
