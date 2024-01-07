package drlc.intermediate.action;

import java.util.Map;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.value.Value;

public class BinaryOpAction extends Action implements IValueAction {
	
	public final @NonNull BinaryActionType type;
	public final DataId target, arg1, arg2;
	
	protected BinaryOpAction(ASTNode<?, ?> node, @NonNull BinaryActionType type, DataId target, DataId arg1, DataId arg2) {
		super(node);
		this.type = type;
		
		if (target == null) {
			throw Helpers.nodeError(node, "Binary op action target was null!");
		}
		else {
			this.target = target;
		}
		
		if (arg1 == null) {
			throw Helpers.nodeError(node, "Binary op action first argument was null!");
		}
		else {
			this.arg1 = arg1;
		}
		
		if (arg2 == null) {
			throw Helpers.nodeError(node, "Binary op action second argument was null!");
		}
		else {
			this.arg2 = arg2;
		}
		
		if (type.isDivideOrRemainder() && arg2 instanceof ValueDataId) {
			ValueDataId valueId = (ValueDataId) arg2;
			if (valueId.typeInfo.isWord() && valueId.value.longValue(node) == 0) {
				throw Helpers.nodeError(node, "Can not divide by zero!");
			}
		}
	}
	
	protected BinaryOpAction copy(DataId target, DataId arg1, DataId arg2) {
		return new BinaryOpAction(null, type, target, arg1, arg2);
	}
	
	protected BinaryOpAction commutated(DataId target, DataId arg1, DataId arg2) {
		BinaryActionType commutatedType = type.commutated();
		return commutatedType == null ? null : commutatedType.action(null, target, arg1, arg2);
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		return new DataId[] {arg1, arg2};
	}
	
	@Override
	public boolean canRemove(boolean compoundReplacement) {
		return false;
	}
	
	@Override
	public boolean canReplaceRvalue() {
		return true;
	}
	
	@Override
	public DataId getRvalueReplacer() {
		return null;
	}
	
	@Override
	public BinaryOpAction replaceRvalue(DataId targetId, DataId rvalueReplacer) {
		DataIdReplaceResult arg1Result = replaceDataId(arg1, targetId, rvalueReplacer), arg2Result = replaceDataId(arg2, targetId, rvalueReplacer);
		if (arg1Result.success || arg2Result.success) {
			return copy(target, arg1Result.dataId, arg2Result.dataId);
		}
		else {
			throw new IllegalArgumentException(String.format("Neither binary op action argument %s, %s matched replacement data ID %s!", arg1, arg2, targetId));
		}
	}
	
	@Override
	public boolean canReplaceLvalue() {
		return true;
	}
	
	@Override
	public DataId getLvalueReplacer() {
		return null;
	}
	
	@Override
	public BinaryOpAction replaceLvalue(DataId targetId, DataId lvalueReplacer) {
		return copy(lvalueReplacer, arg1, arg2);
	}
	
	@Override
	public Action setTransientLvalue() {
		return copy(target.getTransient(null), arg1, arg2);
	}
	
	@Override
	public boolean canReorderRvalues() {
		return type.commutated() != null;
	}
	
	@Override
	public Action swapRvalues(int i, int j) {
		if ((i == 0 && j == 1) || (i == 1 && j == 0)) {
			return commutated(target, arg2, arg1);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Action foldRvalues() {
		if (arg1 instanceof ValueDataId && arg2 instanceof ValueDataId) {
			return new AssignmentAction(null, target, Main.generator.binaryOp(null, ((ValueDataId) arg1).value, type.opType, ((ValueDataId) arg2).value).dataId());
		}
		else {
			return null;
		}
	}
	
	public Action simplify() {
		if (arg1.equals(arg2)) {
			switch (type) {
				case BOOL_EQUAL_TO_BOOL:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case BOOL_NOT_EQUAL_TO_BOOL:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case BOOL_LESS_THAN_BOOL:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case BOOL_LESS_OR_EQUAL_BOOL:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case BOOL_MORE_THAN_BOOL:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case BOOL_MORE_OR_EQUAL_BOOL:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case BOOL_AND_BOOL:
				case BOOL_OR_BOOL:
					return new AssignmentAction(null, target, arg1);
				case BOOL_XOR_BOOL:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				
				case INT_EQUAL_TO_INT:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case INT_NOT_EQUAL_TO_INT:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case INT_LESS_THAN_INT:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case INT_LESS_OR_EQUAL_INT:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case INT_MORE_THAN_INT:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case INT_MORE_OR_EQUAL_INT:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case INT_PLUS_INT:
					return null;
				case INT_AND_INT:
				case INT_OR_INT:
					return new AssignmentAction(null, target, arg1);
				case INT_XOR_INT:
				case INT_MINUS_INT:
					return new AssignmentAction(null, target, (arg1.typeInfo.equals(Main.generator.natTypeInfo) ? Main.generator.natValue(0) : Main.generator.intValue(0)).dataId());
				case INT_MULTIPLY_INT:
					return null;
				case INT_DIVIDE_INT:
					return new AssignmentAction(null, target, Main.generator.intValue(1).dataId());
				case INT_REMAINDER_INT:
					return new AssignmentAction(null, target, Main.generator.intValue(0).dataId());
				case INT_LEFT_SHIFT_INT:
				case INT_RIGHT_SHIFT_INT:
				case INT_LEFT_ROTATE_INT:
				case INT_RIGHT_ROTATE_INT:
					return null;
				
				case NAT_LESS_THAN_NAT:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case NAT_LESS_OR_EQUAL_NAT:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case NAT_MORE_THAN_NAT:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case NAT_MORE_OR_EQUAL_NAT:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case NAT_DIVIDE_NAT:
					return new AssignmentAction(null, target, Main.generator.natValue(1).dataId());
				case NAT_REMAINDER_NAT:
					return new AssignmentAction(null, target, Main.generator.natValue(0).dataId());
				case NAT_RIGHT_SHIFT_INT:
					return null;
				
				case CHAR_EQUAL_TO_CHAR:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case CHAR_NOT_EQUAL_TO_CHAR:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case CHAR_LESS_THAN_CHAR:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case CHAR_LESS_OR_EQUAL_CHAR:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case CHAR_MORE_THAN_CHAR:
					return new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case CHAR_MORE_OR_EQUAL_CHAR:
					return new AssignmentAction(null, target, Main.generator.trueValue.dataId());
			}
		}
		
		Value left = arg1 instanceof ValueDataId ? ((ValueDataId) arg1).value : null, right = arg2 instanceof ValueDataId ? ((ValueDataId) arg2).value : null;
		
		if (left != null && right == null) {
			switch (type) {
				case BOOL_EQUAL_TO_BOOL:
					return left.boolValue(null) ? new AssignmentAction(null, target, arg2) : UnaryActionType.NOT_BOOL.action(null, target, arg2);
				case BOOL_NOT_EQUAL_TO_BOOL:
					return left.boolValue(null) ? UnaryActionType.NOT_BOOL.action(null, target, arg2) : new AssignmentAction(null, target, arg2);
				case BOOL_LESS_THAN_BOOL:
					return left.boolValue(null) ? new AssignmentAction(null, target, Main.generator.falseValue.dataId()) : new AssignmentAction(null, target, arg2);
				case BOOL_LESS_OR_EQUAL_BOOL:
					return left.boolValue(null) ? new AssignmentAction(null, target, arg2) : new AssignmentAction(null, target, Main.generator.trueValue.dataId());
				case BOOL_MORE_THAN_BOOL:
					return left.boolValue(null) ? UnaryActionType.NOT_BOOL.action(null, target, arg2) : new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case BOOL_MORE_OR_EQUAL_BOOL:
					return left.boolValue(null) ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : UnaryActionType.NOT_BOOL.action(null, target, arg2);
				case BOOL_AND_BOOL:
					return left.boolValue(null) ? new AssignmentAction(null, target, arg2) : new AssignmentAction(null, target, Main.generator.falseValue.dataId());
				case BOOL_OR_BOOL:
					return left.boolValue(null) ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : new AssignmentAction(null, target, arg2);
				case BOOL_XOR_BOOL:
					return left.boolValue(null) ? UnaryActionType.NOT_BOOL.action(null, target, arg2) : new AssignmentAction(null, target, arg2);
				
				case INT_EQUAL_TO_INT:
				case INT_NOT_EQUAL_TO_INT:
					return null;
				case INT_LESS_THAN_INT:
					return left.longValue(null) == Long.MAX_VALUE ? new AssignmentAction(null, target, Main.generator.falseValue.dataId()) : null;
				case INT_LESS_OR_EQUAL_INT:
					return left.longValue(null) == Long.MIN_VALUE ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : null;
				case INT_MORE_THAN_INT:
					return left.longValue(null) == Long.MIN_VALUE ? new AssignmentAction(null, target, Main.generator.falseValue.dataId()) : null;
				case INT_MORE_OR_EQUAL_INT:
					return left.longValue(null) == Long.MAX_VALUE ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : null;
				case INT_PLUS_INT:
					return left.longValue(null) == 0 ? new AssignmentAction(null, target, arg2) : null;
				case INT_AND_INT: {
					long longValue = left.longValue(null);
					if (longValue == 0) {
						return new AssignmentAction(null, target, left.dataId());
					}
					else if (longValue == -1) {
						return new AssignmentAction(null, target, arg2);
					}
					else {
						return null;
					}
				}
				case INT_OR_INT: {
					long longValue = left.longValue(null);
					if (longValue == 0) {
						return new AssignmentAction(null, target, arg2);
					}
					else if (longValue == -1) {
						return new AssignmentAction(null, target, left.dataId());
					}
					else {
						return null;
					}
				}
				case INT_XOR_INT: {
					long longValue = left.longValue(null);
					if (longValue == 0) {
						return new AssignmentAction(null, target, arg2);
					}
					else if (longValue == -1) {
						return UnaryActionType.NOT_INT.action(null, target, arg2);
					}
					else {
						return null;
					}
				}
				case INT_MINUS_INT:
					return left.longValue(null) == 0 ? UnaryActionType.MINUS_INT.action(null, target, arg2) : null;
				case INT_MULTIPLY_INT: {
					long longValue = left.longValue(null);
					if (longValue == 0) {
						return new AssignmentAction(null, target, left.dataId());
					}
					else if (longValue == 1) {
						return new AssignmentAction(null, target, arg2);
					}
					else if (longValue == -1) {
						return UnaryActionType.MINUS_INT.action(null, target, arg2);
					}
					else {
						return null;
					}
				}
				case INT_DIVIDE_INT:
				case INT_REMAINDER_INT:
				case INT_LEFT_SHIFT_INT:
					return left.longValue(null) == 0 ? new AssignmentAction(null, target, left.dataId()) : null;
				case INT_RIGHT_SHIFT_INT:
				case INT_LEFT_ROTATE_INT:
				case INT_RIGHT_ROTATE_INT: {
					long longValue = left.longValue(null);
					if (longValue == 0 || longValue == -1) {
						return new AssignmentAction(null, target, left.dataId());
					}
					else {
						return null;
					}
				}
				
				case NAT_LESS_THAN_NAT:
					return left.longValue(null) == -1 ? new AssignmentAction(null, target, Main.generator.falseValue.dataId()) : null;
				case NAT_LESS_OR_EQUAL_NAT:
					return left.longValue(null) == 0 ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : null;
				case NAT_MORE_THAN_NAT:
					return left.longValue(null) == 0 ? new AssignmentAction(null, target, Main.generator.falseValue.dataId()) : null;
				case NAT_MORE_OR_EQUAL_NAT:
					return left.longValue(null) == -1 ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : null;
				case NAT_DIVIDE_NAT:
				case NAT_REMAINDER_NAT:
				case NAT_RIGHT_SHIFT_INT:
					return left.longValue(null) == 0 ? new AssignmentAction(null, target, left.dataId()) : null;
				
				case CHAR_EQUAL_TO_CHAR:
				case CHAR_NOT_EQUAL_TO_CHAR:
					return null;
				case CHAR_LESS_THAN_CHAR:
					return left.charValue(null) == -1 ? new AssignmentAction(null, target, Main.generator.falseValue.dataId()) : null;
				case CHAR_LESS_OR_EQUAL_CHAR:
					return left.charValue(null) == 0 ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : null;
				case CHAR_MORE_THAN_CHAR:
					return left.charValue(null) == 0 ? new AssignmentAction(null, target, Main.generator.falseValue.dataId()) : null;
				case CHAR_MORE_OR_EQUAL_CHAR:
					return left.charValue(null) == -1 ? new AssignmentAction(null, target, Main.generator.trueValue.dataId()) : null;
			}
		}
		else if (left == null && right != null) {
			switch (type) {
				case BOOL_EQUAL_TO_BOOL:
				case BOOL_NOT_EQUAL_TO_BOOL:
				case BOOL_LESS_THAN_BOOL:
				case BOOL_LESS_OR_EQUAL_BOOL:
				case BOOL_MORE_THAN_BOOL:
				case BOOL_MORE_OR_EQUAL_BOOL:
				case BOOL_AND_BOOL:
				case BOOL_OR_BOOL:
				case BOOL_XOR_BOOL:
					return commutated(target, arg2, arg1).simplify();
				
				case INT_EQUAL_TO_INT:
				case INT_NOT_EQUAL_TO_INT:
				case INT_LESS_THAN_INT:
				case INT_LESS_OR_EQUAL_INT:
				case INT_MORE_THAN_INT:
				case INT_MORE_OR_EQUAL_INT:
				case INT_PLUS_INT:
				case INT_AND_INT:
				case INT_OR_INT:
				case INT_XOR_INT:
					return commutated(target, arg2, arg1).simplify();
				case INT_MINUS_INT:
					return right.longValue(null) == 0 ? new AssignmentAction(null, target, arg1) : null;
				case INT_MULTIPLY_INT:
					return commutated(target, arg2, arg1).simplify();
				case INT_DIVIDE_INT: {
					long longValue = right.longValue(null);
					if (longValue == 1) {
						return new AssignmentAction(null, target, arg1);
					}
					else if (longValue == -1) {
						return UnaryActionType.MINUS_INT.action(null, target, arg1);
					}
					else {
						return null;
					}
				}
				case INT_REMAINDER_INT: {
					long longValue = right.longValue(null);
					if (longValue == 1 || longValue == -1) {
						return new AssignmentAction(null, target, Main.generator.intValue(0).dataId());
					}
					else {
						return null;
					}
				}
				case INT_LEFT_SHIFT_INT:
				case INT_RIGHT_SHIFT_INT:
				case INT_LEFT_ROTATE_INT:
				case INT_RIGHT_ROTATE_INT:
					return right.longValue(null) == 0 ? new AssignmentAction(null, target, arg1) : null;
				
				case NAT_LESS_THAN_NAT:
				case NAT_LESS_OR_EQUAL_NAT:
				case NAT_MORE_THAN_NAT:
				case NAT_MORE_OR_EQUAL_NAT:
					return commutated(target, arg2, arg1).simplify();
				case NAT_DIVIDE_NAT:
					return right.longValue(null) == 1 ? new AssignmentAction(null, target, arg1) : null;
				case NAT_REMAINDER_NAT:
					return right.longValue(null) == 1 ? new AssignmentAction(null, target, Main.generator.natValue(0).dataId()) : null;
				case NAT_RIGHT_SHIFT_INT:
					return right.longValue(null) == 0 ? new AssignmentAction(null, target, arg1) : null;
				
				case CHAR_EQUAL_TO_CHAR:
				case CHAR_NOT_EQUAL_TO_CHAR:
				case CHAR_LESS_THAN_CHAR:
				case CHAR_LESS_OR_EQUAL_CHAR:
				case CHAR_MORE_THAN_CHAR:
				case CHAR_MORE_OR_EQUAL_CHAR:
					return commutated(target, arg2, arg1).simplify();
			}
		}
		
		return null;
	}
	
	@Override
	public Action replaceRegIds(Map<Long, Long> regIdMap) {
		DataIdReplaceResult targetResult = replaceRegId(target, regIdMap), arg1Result = replaceRegId(arg1, regIdMap), arg2Result = replaceRegId(arg2, regIdMap);
		if (targetResult.success || arg1Result.success || arg2Result.success) {
			return copy(targetResult.dataId, arg1Result.dataId, arg2Result.dataId);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return target + " = " + arg1 + " " + type + " " + arg2;
	}
}
