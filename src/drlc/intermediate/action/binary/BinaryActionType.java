package drlc.intermediate.action.binary;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
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
	INT_LEFT_SHIFT_INT(BinaryOpType.LEFT_SHIFT),
	INT_RIGHT_SHIFT_INT(BinaryOpType.RIGHT_SHIFT),
	INT_LEFT_ROTATE_INT(BinaryOpType.LEFT_ROTATE),
	INT_RIGHT_ROTATE_INT(BinaryOpType.RIGHT_ROTATE),
	INT_MULTIPLY_INT(BinaryOpType.MULTIPLY),
	INT_DIVIDE_INT(BinaryOpType.DIVIDE),
	INT_REMAINDER_INT(BinaryOpType.REMAINDER),
	
	NAT_LESS_THAN_NAT(BinaryOpType.LESS_THAN),
	NAT_LESS_OR_EQUAL_NAT(BinaryOpType.LESS_OR_EQUAL),
	NAT_MORE_THAN_NAT(BinaryOpType.MORE_THAN),
	NAT_MORE_OR_EQUAL_NAT(BinaryOpType.MORE_OR_EQUAL),
	NAT_RIGHT_SHIFT_INT(BinaryOpType.RIGHT_SHIFT),
	NAT_DIVIDE_NAT(BinaryOpType.DIVIDE),
	NAT_REMAINDER_NAT(BinaryOpType.REMAINDER),
	
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
		switch (this) {
			case BOOL_EQUAL_TO_BOOL:
				return new BinaryBoolEqualToBoolAction(node, target, arg1, arg2);
			case BOOL_NOT_EQUAL_TO_BOOL:
				return new BinaryBoolNotEqualToBoolAction(node, target, arg1, arg2);
			case BOOL_LESS_THAN_BOOL:
				return new BinaryBoolLessThanBoolAction(node, target, arg1, arg2);
			case BOOL_LESS_OR_EQUAL_BOOL:
				return new BinaryBoolLessOrEqualBoolAction(node, target, arg1, arg2);
			case BOOL_MORE_THAN_BOOL:
				return new BinaryBoolMoreThanBoolAction(node, target, arg1, arg2);
			case BOOL_MORE_OR_EQUAL_BOOL:
				return new BinaryBoolMoreOrEqualBoolAction(node, target, arg1, arg2);
			case BOOL_AND_BOOL:
				return new BinaryBoolAndBoolAction(node, target, arg1, arg2);
			case BOOL_OR_BOOL:
				return new BinaryBoolOrBoolAction(node, target, arg1, arg2);
			case BOOL_XOR_BOOL:
				return new BinaryBoolXorBoolAction(node, target, arg1, arg2);
			
			case INT_EQUAL_TO_INT:
				return new BinaryIntEqualToIntAction(node, target, arg1, arg2);
			case INT_NOT_EQUAL_TO_INT:
				return new BinaryIntNotEqualToIntAction(node, target, arg1, arg2);
			case INT_LESS_THAN_INT:
				return new BinaryIntLessThanIntAction(node, target, arg1, arg2);
			case INT_LESS_OR_EQUAL_INT:
				return new BinaryIntLessOrEqualIntAction(node, target, arg1, arg2);
			case INT_MORE_THAN_INT:
				return new BinaryIntMoreThanIntAction(node, target, arg1, arg2);
			case INT_MORE_OR_EQUAL_INT:
				return new BinaryIntMoreOrEqualIntAction(node, target, arg1, arg2);
			case INT_PLUS_INT:
				return new BinaryIntPlusIntAction(node, target, arg1, arg2);
			case INT_AND_INT:
				return new BinaryIntAndIntAction(node, target, arg1, arg2);
			case INT_OR_INT:
				return new BinaryIntOrIntAction(node, target, arg1, arg2);
			case INT_XOR_INT:
				return new BinaryIntXorIntAction(node, target, arg1, arg2);
			case INT_MINUS_INT:
				return new BinaryIntMinusIntAction(node, target, arg1, arg2);
			case INT_LEFT_SHIFT_INT:
				return new BinaryIntLeftShiftIntAction(node, target, arg1, arg2);
			case INT_RIGHT_SHIFT_INT:
				return new BinaryIntRightShiftIntAction(node, target, arg1, arg2);
			case INT_LEFT_ROTATE_INT:
				return new BinaryIntLeftRotateIntAction(node, target, arg1, arg2);
			case INT_RIGHT_ROTATE_INT:
				return new BinaryIntRightRotateIntAction(node, target, arg1, arg2);
			case INT_MULTIPLY_INT:
				return new BinaryIntMultiplyIntAction(node, target, arg1, arg2);
			case INT_DIVIDE_INT:
				return new BinaryIntDivideIntAction(node, target, arg1, arg2);
			case INT_REMAINDER_INT:
				return new BinaryIntRemainderIntAction(node, target, arg1, arg2);
			
			case NAT_LESS_THAN_NAT:
				return new BinaryNatLessThanNatAction(node, target, arg1, arg2);
			case NAT_LESS_OR_EQUAL_NAT:
				return new BinaryNatLessOrEqualNatAction(node, target, arg1, arg2);
			case NAT_MORE_THAN_NAT:
				return new BinaryNatMoreThanNatAction(node, target, arg1, arg2);
			case NAT_MORE_OR_EQUAL_NAT:
				return new BinaryNatMoreOrEqualNatAction(node, target, arg1, arg2);
			case NAT_RIGHT_SHIFT_INT:
				return new BinaryNatRightShiftIntAction(node, target, arg1, arg2);
			case NAT_DIVIDE_NAT:
				return new BinaryNatDivideNatAction(node, target, arg1, arg2);
			case NAT_REMAINDER_NAT:
				return new BinaryNatRemainderNatAction(node, target, arg1, arg2);
			
			case CHAR_EQUAL_TO_CHAR:
				return new BinaryCharEqualToCharAction(node, target, arg1, arg2);
			case CHAR_NOT_EQUAL_TO_CHAR:
				return new BinaryCharNotEqualToCharAction(node, target, arg1, arg2);
			case CHAR_LESS_THAN_CHAR:
				return new BinaryCharLessThanCharAction(node, target, arg1, arg2);
			case CHAR_LESS_OR_EQUAL_CHAR:
				return new BinaryCharLessOrEqualCharAction(node, target, arg1, arg2);
			case CHAR_MORE_THAN_CHAR:
				return new BinaryCharMoreThanCharAction(node, target, arg1, arg2);
			case CHAR_MORE_OR_EQUAL_CHAR:
				return new BinaryCharMoreOrEqualCharAction(node, target, arg1, arg2);
			default:
				throw Helpers.nodeError(node, "Attempted to write an expression including a binary op of unknown type!");
		}
	}
}
