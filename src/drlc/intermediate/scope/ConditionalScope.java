package drlc.intermediate.scope;

import java.util.stream.Stream;

import drlc.intermediate.component.Variable;

public class ConditionalScope extends Scope {
	
	protected final boolean hasElseBranch;
	
	public ConditionalScope(Scope parent, boolean hasElseBranch) {
		super(parent);
		this.hasElseBranch = hasElseBranch;
	}
	
	protected Stream<Scope> branchingChildren() {
		return children.stream().filter(x -> !x.definiteExecution);
	}
	
	@Override
	public boolean hasDefiniteReturn() {
		return super.hasDefiniteReturn() || (hasElseBranch && branchingChildren().allMatch(Scope::hasDefiniteReturn));
	}
	
	@Override
	protected boolean isVariablePotentiallyInitializedInternal(Variable variable, Scope location) {
		return initializationSet.contains(variable) || children.stream().anyMatch(x -> (x.definiteExecution || location.isSubScope(x)) && x.isVariablePotentiallyInitializedInternal(variable, location));
	}
	
	@Override
	protected boolean isVariableDefinitelyInitializedInternal(Variable variable) {
		return super.isVariableDefinitelyInitializedInternal(variable) || (hasElseBranch && branchingChildren().allMatch(x -> x.isVariableDefinitelyInitializedInternal(variable)));
	}
}
