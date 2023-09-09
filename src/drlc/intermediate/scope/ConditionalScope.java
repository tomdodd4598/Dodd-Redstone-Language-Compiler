package drlc.intermediate.scope;

public class ConditionalScope extends Scope {
	
	public final boolean hasEnd;
	
	public ConditionalScope(Scope parent, boolean hasEnd) {
		super(parent);
		this.hasEnd = hasEnd;
	}
	
	@Override
	public boolean checkCompleteReturn() {
		return definiteLocalReturn || (hasEnd && children.stream().allMatch(Scope::checkCompleteReturn));
	}
}
