package drlc.intermediate.action;

public interface IJumpAction {
	
	public boolean isConditional();
	
	public int getTarget();
	
	public Action copy(int target);
}
