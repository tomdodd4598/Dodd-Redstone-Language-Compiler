package drlc.interpret.action;

public class NoOpAction extends Action {
	
	public NoOpAction() {
		super(null);
	}
	
	@Override
	public String toString() {
		throw new UnsupportedOperationException(String.format("No op action not correctly removed!"));
	}
}
