package drlc.intermediate.action.unary;

import drlc.intermediate.component.DataId;
import drlc.node.Node;

public enum UnaryActionType {
	
	MINUS_INT,
	NOT_BOOL,
	NOT_INT;
	
	public UnaryOpAction action(Node node, DataId target, DataId arg) {
		switch (this) {
			case MINUS_INT:
				return new UnaryMinusIntAction(node, target, arg);
			case NOT_BOOL:
				return new UnaryNotBoolAction(node, target, arg);
			case NOT_INT:
				return new UnaryNotIntAction(node, target, arg);
			default:
				throw new IllegalArgumentException(String.format("Attempted to write an expression including a unary op of unknown type! %s", node));
		}
	}
}
