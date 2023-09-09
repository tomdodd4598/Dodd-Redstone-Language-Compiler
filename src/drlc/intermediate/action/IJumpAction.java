package drlc.intermediate.action;

public interface IJumpAction {
	
	public boolean isConditional();
	
	public String getTarget();
	
	public Action copy(String target);
}
