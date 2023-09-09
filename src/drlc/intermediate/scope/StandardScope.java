package drlc.intermediate.scope;

public class StandardScope extends Scope {
	
	public StandardScope(Scope parent) {
		super(parent);
	}
	
	@Override
	public boolean checkCompleteReturn() {
		return definiteLocalReturn || children.stream().anyMatch(Scope::checkCompleteReturn);
	}
}
