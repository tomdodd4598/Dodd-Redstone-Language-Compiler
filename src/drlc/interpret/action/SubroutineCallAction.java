package drlc.interpret.action;

import drlc.node.Node;

public abstract class SubroutineCallAction extends Action {
	
	public final String name;
	public final String[] args;
	
	public SubroutineCallAction(Node node, String name, String... args) {
		super(node);
		if (name == null) {
			throw new IllegalArgumentException(String.format("Subroutine call action name was null! %s", node));
		}
		else {
			this.name = name;
		}
		if (args == null) {
			throw new IllegalArgumentException(String.format("Subroutine call action arguments were null! %s", node));
		}
		else {
			this.args = args;
		}
	}
	
}
