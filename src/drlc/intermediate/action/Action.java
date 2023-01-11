package drlc.intermediate.action;

import drlc.node.Node;

public abstract class Action {
	
	public Action(Node node) {}
	
	@Override
	public abstract String toString();
}
