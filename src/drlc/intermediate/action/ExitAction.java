package drlc.intermediate.action;

import drlc.Global;

public class ExitAction extends BasicAction implements IDefiniteRedirectAction {
	
	public ExitAction() {
		super(null, Global.EXIT);
	}
}
