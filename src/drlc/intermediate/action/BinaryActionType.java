package drlc.intermediate.action;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.BinaryOpType;
import drlc.intermediate.component.data.DataId;

public enum BinaryActionType {
	
	BOOL_EQUAL_TO_BOOL(BinaryOpType.EQUAL_TO),
	BOOL_NOT_EQUAL_TO_BOOL(BinaryOpType.NOT_EQUAL_TO),
	BOOL_LESS_THAN_BOOL(BinaryOpType.LESS_THAN),
	BOOL_LESS_OR_EQUAL_BOOL(BinaryOpType.LESS_OR_EQUAL),
	BOOL_MORE_THAN_BOOL(BinaryOpType.MORE_THAN),
	BOOL_MORE_OR_EQUAL_BOOL(BinaryOpType.MORE_OR_EQUAL),
	BOOL_AND_BOOL(BinaryOpType.AND),
	BOOL_OR_BOOL(BinaryOpType.OR),
	BOOL_XOR_BOOL(BinaryOpType.XOR),
	
	INT_EQUAL_TO_INT(BinaryOpType.EQUAL_TO),
	INT_NOT_EQUAL_TO_INT(BinaryOpType.NOT_EQUAL_TO),
	INT_LESS_THAN_INT(BinaryOpType.LESS_THAN),
	INT_LESS_OR_EQUAL_INT(BinaryOpType.LESS_OR_EQUAL),
	INT_MORE_THAN_INT(BinaryOpType.MORE_THAN),
	INT_MORE_OR_EQUAL_INT(BinaryOpType.MORE_OR_EQUAL),
	INT_PLUS_INT(BinaryOpType.PLUS),
	INT_AND_INT(BinaryOpType.AND),
	INT_OR_INT(BinaryOpType.OR),
	INT_XOR_INT(BinaryOpType.XOR),
	INT_MINUS_INT(BinaryOpType.MINUS),
	INT_MULTIPLY_INT(BinaryOpType.MULTIPLY),
	INT_DIVIDE_INT(BinaryOpType.DIVIDE),
	INT_REMAINDER_INT(BinaryOpType.REMAINDER),
	INT_LEFT_SHIFT_INT(BinaryOpType.LEFT_SHIFT),
	INT_RIGHT_SHIFT_INT(BinaryOpType.RIGHT_SHIFT),
	INT_LEFT_ROTATE_INT(BinaryOpType.LEFT_ROTATE),
	INT_RIGHT_ROTATE_INT(BinaryOpType.RIGHT_ROTATE),
	
	NAT_LESS_THAN_NAT(BinaryOpType.LESS_THAN),
	NAT_LESS_OR_EQUAL_NAT(BinaryOpType.LESS_OR_EQUAL),
	NAT_MORE_THAN_NAT(BinaryOpType.MORE_THAN),
	NAT_MORE_OR_EQUAL_NAT(BinaryOpType.MORE_OR_EQUAL),
	NAT_DIVIDE_NAT(BinaryOpType.DIVIDE),
	NAT_REMAINDER_NAT(BinaryOpType.REMAINDER),
	NAT_RIGHT_SHIFT_INT(BinaryOpType.RIGHT_SHIFT),
	
	CHAR_EQUAL_TO_CHAR(BinaryOpType.EQUAL_TO),
	CHAR_NOT_EQUAL_TO_CHAR(BinaryOpType.NOT_EQUAL_TO),
	CHAR_LESS_THAN_CHAR(BinaryOpType.LESS_THAN),
	CHAR_LESS_OR_EQUAL_CHAR(BinaryOpType.LESS_OR_EQUAL),
	CHAR_MORE_THAN_CHAR(BinaryOpType.MORE_THAN),
	CHAR_MORE_OR_EQUAL_CHAR(BinaryOpType.MORE_OR_EQUAL);
	
	public final @NonNull BinaryOpType opType;
	
	private BinaryActionType(@NonNull BinaryOpType opType) {
		this.opType = opType;
	}
	
	public BinaryOpAction action(ASTNode<?, ?> node, DataId target, DataId arg1, DataId arg2) {
		return new BinaryOpAction(node, this, target, arg1, arg2);
	}
	
	protected BinaryActionType commutated() {
		switch (this) {
			case BOOL_EQUAL_TO_BOOL:
			case BOOL_NOT_EQUAL_TO_BOOL:
				return this;
			case BOOL_LESS_THAN_BOOL:
				return BOOL_MORE_THAN_BOOL;
			case BOOL_LESS_OR_EQUAL_BOOL:
				return BOOL_MORE_OR_EQUAL_BOOL;
			case BOOL_MORE_THAN_BOOL:
				return BOOL_LESS_THAN_BOOL;
			case BOOL_MORE_OR_EQUAL_BOOL:
				return BOOL_LESS_OR_EQUAL_BOOL;
			case BOOL_AND_BOOL:
			case BOOL_OR_BOOL:
			case BOOL_XOR_BOOL:
				return this;
			
			case INT_EQUAL_TO_INT:
			case INT_NOT_EQUAL_TO_INT:
				return this;
			case INT_LESS_THAN_INT:
				return INT_MORE_THAN_INT;
			case INT_LESS_OR_EQUAL_INT:
				return INT_MORE_OR_EQUAL_INT;
			case INT_MORE_THAN_INT:
				return INT_LESS_THAN_INT;
			case INT_MORE_OR_EQUAL_INT:
				return INT_LESS_OR_EQUAL_INT;
			case INT_PLUS_INT:
			case INT_AND_INT:
			case INT_OR_INT:
			case INT_XOR_INT:
				return this;
			case INT_MINUS_INT:
				return null;
			case INT_MULTIPLY_INT:
				return this;
			case INT_DIVIDE_INT:
			case INT_REMAINDER_INT:
			case INT_LEFT_SHIFT_INT:
			case INT_RIGHT_SHIFT_INT:
			case INT_LEFT_ROTATE_INT:
			case INT_RIGHT_ROTATE_INT:
				return null;
			
			case NAT_LESS_THAN_NAT:
				return NAT_MORE_THAN_NAT;
			case NAT_LESS_OR_EQUAL_NAT:
				return NAT_MORE_OR_EQUAL_NAT;
			case NAT_MORE_THAN_NAT:
				return NAT_LESS_THAN_NAT;
			case NAT_MORE_OR_EQUAL_NAT:
				return NAT_LESS_OR_EQUAL_NAT;
			case NAT_DIVIDE_NAT:
			case NAT_REMAINDER_NAT:
			case NAT_RIGHT_SHIFT_INT:
				return null;
			
			case CHAR_EQUAL_TO_CHAR:
			case CHAR_NOT_EQUAL_TO_CHAR:
				return this;
			case CHAR_LESS_THAN_CHAR:
				return CHAR_MORE_THAN_CHAR;
			case CHAR_LESS_OR_EQUAL_CHAR:
				return CHAR_MORE_OR_EQUAL_CHAR;
			case CHAR_MORE_THAN_CHAR:
				return CHAR_LESS_THAN_CHAR;
			case CHAR_MORE_OR_EQUAL_CHAR:
				return CHAR_LESS_OR_EQUAL_CHAR;
		}
		return null;
	}
	
	public boolean isDivideOrRemainder() {
		switch (this) {
			case INT_DIVIDE_INT:
			case INT_REMAINDER_INT:
			case NAT_DIVIDE_NAT:
			case NAT_REMAINDER_NAT:
				return true;
			default:
				return false;
		}
	}
	
	@Override
	public String toString() {
		return opType.toString();
	}
}
