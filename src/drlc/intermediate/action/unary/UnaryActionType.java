package drlc.intermediate.action.unary;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.UnaryOpType;
import drlc.intermediate.component.data.DataId;

public enum UnaryActionType {
	
	MINUS_INT(UnaryOpType.MINUS),
	NOT_BOOL(UnaryOpType.NOT),
	NOT_INT(UnaryOpType.NOT);
	
	public final @NonNull UnaryOpType opType;
	
	private UnaryActionType(@NonNull UnaryOpType opType) {
		this.opType = opType;
	}
	
	public UnaryOpAction action(ASTNode<?, ?> node, DataId target, DataId arg) {
		switch (this) {
			case MINUS_INT:
				return new UnaryMinusIntAction(node, target, arg);
			case NOT_BOOL:
				return new UnaryNotBoolAction(node, target, arg);
			case NOT_INT:
				return new UnaryNotIntAction(node, target, arg);
			default:
				throw Helpers.nodeError(node, "Attempted to write an expression including a unary op of unknown type!");
		}
	}
}
