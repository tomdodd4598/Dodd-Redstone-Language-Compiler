package drlc.intermediate.scope;

import java.util.stream.Stream;

import org.eclipse.jdt.annotation.Nullable;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Variable;

public class ConditionalScope extends Scope {
	
	protected final boolean hasElseBranch;
	
	public ConditionalScope(ASTNode<?> node, @Nullable String name, @Nullable Scope parent, boolean pseudo, boolean hasElseBranch) {
		super(node, name, parent, pseudo);
		this.hasElseBranch = hasElseBranch;
	}
	
	protected Stream<Scope> branchingChildren() {
		return childMap.values().stream().filter(x -> !x.definiteExecution);
	}
	
	@Override
	public boolean hasDefiniteReturn() {
		return super.hasDefiniteReturn() || (hasElseBranch && branchingChildren().allMatch(Scope::hasDefiniteReturn));
	}
	
	@Override
	protected boolean isVariablePotentiallyInitializedInternal(Variable variable, Scope location) {
		return initializationSet.contains(variable) || childMap.values().stream().anyMatch(x -> (x.definiteExecution || x.isSubScopeOf(location)) && x.isVariablePotentiallyInitializedInternal(variable, location));
	}
	
	@Override
	protected boolean isVariableDefinitelyInitializedInternal(Variable variable, Scope location) {
		return super.isVariableDefinitelyInitializedInternal(variable, location) || (hasElseBranch && branchingChildren().allMatch(x -> x.isVariableDefinitelyInitializedInternal(variable, location)));
	}
}
