package drlc.intermediate.action;

import drlc.node.Node;

public class PlaceholderAction extends Action {
	
	public final String type;
	
	public PlaceholderAction(Node node, String type) {
		super(node);
		if (type == null) {
			throw new IllegalArgumentException(String.format("Placeholder action type was null! %s", node));
		}
		else {
			this.type = type;
		}
	}
	
	@Override
	public String toString() {
		throw new UnsupportedOperationException(String.format("Placeholder action not correctly substituted!"));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlaceholderAction) {
			return type.equals(((PlaceholderAction) obj).type);
		}
		else {
			return false;
		}
	}
}
