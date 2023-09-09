package drlc.intermediate.action;

import drlc.intermediate.ast.ASTNode;

public class PlaceholderAction extends Action {
	
	public final String type;
	
	public PlaceholderAction(ASTNode node, String type) {
		super(node);
		if (type == null) {
			throw node.error("Placeholder action type was null!");
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
