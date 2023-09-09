package drlc.intermediate.scope;

public class IterativeScope extends Scope {
	
	public final boolean definiteExecution;
	
	public IterativeScope(Scope parent, boolean definiteExecution) {
		super(parent);
		this.definiteExecution = definiteExecution;
	}
	
	@Override
	public boolean checkCompleteReturn() {
		return definiteExecution && (definiteLocalReturn || children.stream().anyMatch(Scope::checkCompleteReturn));
	}
	
	@Override
	public boolean isBreakable() {
		return true;
	}
}
