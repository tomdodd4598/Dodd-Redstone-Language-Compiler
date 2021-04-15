package drlc.interpret.action;

import drlc.Global;

public class ReturnAction extends Action implements IStopAction {
	
	public ReturnAction() {
		super(null);
	}
	
	@Override
	public String toString() {
		return Global.RETURN;
	}
}
