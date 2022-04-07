package drlc.interpret.action;

import drlc.Global;

public class ExitAction extends BasicAction implements IDefiniteRedirectAction {
	
	public ExitAction() {
		super(null, Global.EXIT);
	}
}
