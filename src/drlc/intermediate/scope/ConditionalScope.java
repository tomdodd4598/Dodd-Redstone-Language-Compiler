package drlc.intermediate.scope;

public class ConditionalScope extends Scope {
	
	protected final boolean hasElseBranch;
	
	public ConditionalScope(Scope parent, boolean hasElseBranch) {
		super(parent);
		this.hasElseBranch = hasElseBranch;
	}
	
	@Override
	public boolean hasDefiniteReturn() {
		return definiteLocalReturn || (hasElseBranch && children.stream().allMatch(Scope::hasDefiniteReturn));
	}
}
