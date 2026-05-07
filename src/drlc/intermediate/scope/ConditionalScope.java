package drlc.intermediate.scope;

import java.util.Set;
import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Variable;

public class ConditionalScope extends Scope {
	
	protected final boolean hasElseBranch;
	
	public ConditionalScope(ASTNode<?> node, @Nullable String name, @Nullable Scope parent, boolean concrete, boolean hasElseBranch) {
		super(node, name, parent, concrete);
		this.hasElseBranch = hasElseBranch;
	}
	
	protected Stream<Scope> branchingChildren() {
		return childMap.values().stream().filter(x -> !x.definiteExecution);
	}
	
	@Override
	protected boolean hasDefiniteReturnInternal(Set<Scope> path) {
		if (!path.add(this)) {
			return false;
		}
		try {
			return definiteLocalReturn || childMap.values().stream().anyMatch(x -> x.definiteExecution && x.hasDefiniteReturnInternal(path)) || (hasElseBranch && branchingChildren().allMatch(x -> x.hasDefiniteReturnInternal(path)));
		}
		finally {
			path.remove(this);
		}
	}
	
	@Override
	protected boolean isVariablePotentiallyInitializedInternal(Variable variable, Scope location, Set<Scope> path) {
		if (!path.add(this)) {
			return false;
		}
		try {
			return initializationSet.contains(variable) || childMap.values().stream().anyMatch(x -> (x.definiteExecution || location.isSubScopeOf(x) || !location.isSubScopeOf(this)) && x.isVariablePotentiallyInitializedInternal(variable, location, path));
		}
		finally {
			path.remove(this);
		}
	}
	
	@Override
	protected boolean isVariableDefinitelyInitializedInternal(Variable variable, Scope location, Set<Scope> path) {
		if (!path.add(this)) {
			return false;
		}
		try {
			return initializationSet.contains(variable) || childMap.values().stream().anyMatch(x -> (x.definiteExecution || location.isSubScopeOf(x)) && x.isVariableDefinitelyInitializedInternal(variable, location, path)) || (hasElseBranch && branchingChildren().allMatch(x -> x.isVariableDefinitelyInitializedInternal(variable, location, path)));
		}
		finally {
			path.remove(this);
		}
	}
}
