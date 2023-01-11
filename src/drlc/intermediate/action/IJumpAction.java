package drlc.intermediate.action;

public interface IJumpAction<T extends Action & IJumpAction<T>> {
	
	public boolean isConditional();
	
	public String getTarget();
	
	public T copy(String target);
}
