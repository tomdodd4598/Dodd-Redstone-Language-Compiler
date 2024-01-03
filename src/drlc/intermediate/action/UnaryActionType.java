package drlc.intermediate.action;

import org.eclipse.jdt.annotation.NonNull;

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
		return new UnaryOpAction(node, this, target, arg);
	}
	
	@Override
	public String toString() {
		return opType.toString();
	}
}
