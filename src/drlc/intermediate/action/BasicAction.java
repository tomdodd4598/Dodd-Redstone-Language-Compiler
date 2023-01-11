package drlc.intermediate.action;

import drlc.node.Node;

public abstract class BasicAction extends Action {
	
	public final String arg;
	
	public BasicAction(Node node, String arg) {
		super(node);
		if (arg == null) {
			throw new IllegalArgumentException(String.format("Basic action argument was null! %s", node));
		}
		else {
			this.arg = arg;
		}
	}
	
	@Override
	public String toString() {
		return arg;
	}
}
