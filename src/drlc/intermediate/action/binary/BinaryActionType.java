package drlc.intermediate.action.binary;

import drlc.intermediate.component.DataId;
import drlc.node.Node;

public enum BinaryActionType {
	
	BOOL_EQUAL_TO_BOOL,
	BOOL_NOT_EQUAL_TO_BOOL,
	BOOL_LESS_THAN_BOOL,
	BOOL_LESS_OR_EQUAL_BOOL,
	BOOL_MORE_THAN_BOOL,
	BOOL_MORE_OR_EQUAL_BOOL,
	BOOL_AND_BOOL,
	BOOL_OR_BOOL,
	BOOL_XOR_BOOL,
	BOOL_MULTIPLY_BOOL,
	
	INT_EQUAL_TO_INT,
	INT_NOT_EQUAL_TO_INT,
	INT_LESS_THAN_INT,
	INT_LESS_OR_EQUAL_INT,
	INT_MORE_THAN_INT,
	INT_MORE_OR_EQUAL_INT,
	INT_PLUS_INT,
	INT_AND_INT,
	INT_OR_INT,
	INT_XOR_INT,
	INT_MINUS_INT,
	INT_LEFT_SHIFT_INT,
	INT_RIGHT_SHIFT_INT,
	INT_LEFT_ROTATE_INT,
	INT_RIGHT_ROTATE_INT,
	INT_MULTIPLY_INT,
	INT_DIVIDE_INT,
	INT_REMAINDER_INT,
	
	NAT_LESS_THAN_NAT,
	NAT_LESS_OR_EQUAL_NAT,
	NAT_MORE_THAN_NAT,
	NAT_MORE_OR_EQUAL_NAT,
	NAT_RIGHT_SHIFT_INT,
	NAT_DIVIDE_NAT,
	NAT_REMAINDER_NAT,
	
	CHAR_EQUAL_TO_CHAR,
	CHAR_NOT_EQUAL_TO_CHAR,
	CHAR_LESS_THAN_CHAR,
	CHAR_LESS_OR_EQUAL_CHAR,
	CHAR_MORE_THAN_CHAR,
	CHAR_MORE_OR_EQUAL_CHAR;
	
	public BinaryOpAction action(Node node, DataId target, DataId arg1, DataId arg2) {
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
			case BOOL_MULTIPLY_BOOL:
				return new BinaryBoolMultiplyBoolAction(node, target, arg1, arg2);
			
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
				throw new IllegalArgumentException(String.format("Attempted to write an expression including a binary op of unknown type! %s", node));
		}
	}
}
