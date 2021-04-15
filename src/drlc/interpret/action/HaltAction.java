package drlc.interpret.action;

import drlc.Global;

public class HaltAction extends BasicAction implements IStopAction {
	
	public HaltAction() {
		super(null, Global.HALT);
	}
}
