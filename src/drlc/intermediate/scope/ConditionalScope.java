package drlc.intermediate.scope;

public class ConditionalScope extends Scope {
	
	public final boolean definiteExecution;
	
	public ConditionalScope(Scope parent, boolean definiteExecution) {
		super(parent);
		this.definiteExecution = definiteExecution;
	}
	
	@Override
	public boolean checkCompleteReturn() {
		return definiteExecution && (definiteLocalReturn || children.stream().allMatch(Scope::checkCompleteReturn));
	}
}
