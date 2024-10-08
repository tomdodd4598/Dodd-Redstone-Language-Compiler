package drlc.low.drc1;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;

public abstract class RedstoneGenerator extends Generator {
	
	protected RedstoneCode code = new RedstoneCode(false);
	
	public RedstoneGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInConstants() {
		super.addBuiltInConstants();
	}
	
	@Override
	public void addBuiltInVariables() {
		super.addBuiltInVariables();
	}
	
	@Override
	public void addBuiltInFunctions() {
		super.addBuiltInFunctions();
	}
	
	@Override
	public @NonNull IntValue intValue(long value) {
		return new IntValue(null, (short) value);
	}
	
	@Override
	public @NonNull NatValue natValue(long value) {
		return new NatValue(null, (short) value);
	}
	
	@Override
	public int getWordSize() {
		return 1;
	}
	
	@Override
	public int getFunctionSize() {
		return getWordSize();
	}
	
	@Override
	public int getAddressSize() {
		return getWordSize();
	}
	
	@Override
	public @NonNull Function getBuiltInFunction(ASTNode<?> node, String name) {
		if (!Main.rootScope.functionExists(name, false)) {
			switch (name) {
				case Global.NAT_RIGHT_SHIFT_INT:
					addBuiltInFunction(name, natTypeInfo, Helpers.builtInDeclarator("x", natTypeInfo), Helpers.builtInDeclarator("y", intTypeInfo));
					break;
				case Global.INT_LEFT_ROTATE_INT:
				case Global.INT_RIGHT_ROTATE_INT:
				case Global.INT_COMPARE_INT:
					addBuiltInFunction(name, intTypeInfo, Helpers.builtInDeclarator("x", intTypeInfo), Helpers.builtInDeclarator("y", intTypeInfo));
					break;
				case Global.NAT_COMPARE_NAT:
					addBuiltInFunction(name, intTypeInfo, Helpers.builtInDeclarator("x", natTypeInfo), Helpers.builtInDeclarator("y", natTypeInfo));
					break;
				case Global.PRINT_DIGITS:
					addBuiltInFunction(name, unitTypeInfo, Helpers.builtInDeclarator("x", intTypeInfo), Helpers.builtInDeclarator("t", intTypeInfo), Helpers.builtInDeclarator("b", intTypeInfo));
					break;
			}
		}
		
		@NonNull Function function = super.getBuiltInFunction(node, name);
		if (!code.routineExists(function)) {
			code.addRoutine(function, Main.rootScope.getRoutine(node, function));
		}
		return function;
	}
	
	@Override
	public @NonNull Value<?> intIntBinaryOp(ASTNode<?> node, IntValue left, @NonNull BinaryOpType opType, IntValue right) {
		short leftShort = left.shortValue(node), rightShort = right.shortValue(node);
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
				throw undefinedBinaryOp(node, left.typeInfo, opType, right.typeInfo);
			case EQUAL_TO:
				return boolValue(leftShort == rightShort);
			case NOT_EQUAL_TO:
				return boolValue(leftShort != rightShort);
			case LESS_THAN:
				return boolValue(leftShort < rightShort);
			case LESS_OR_EQUAL:
				return boolValue(leftShort <= rightShort);
			case MORE_THAN:
				return boolValue(leftShort > rightShort);
			case MORE_OR_EQUAL:
				return boolValue(leftShort >= rightShort);
			case PLUS:
				return intValue(leftShort + rightShort);
			case AND:
				return intValue(leftShort & rightShort);
			case OR:
				return intValue(leftShort | rightShort);
			case XOR:
				return intValue(leftShort ^ rightShort);
			case MINUS:
				return intValue(leftShort - rightShort);
			case MULTIPLY:
				return intValue(leftShort * rightShort);
			case DIVIDE:
				if (rightShort == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return intValue(leftShort / rightShort);
			case REMAINDER:
				if (rightShort == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return intValue(leftShort % rightShort);
			case LEFT_SHIFT:
				return intValue(leftShort << rightShort);
			case RIGHT_SHIFT:
				return intValue(leftShort >> rightShort);
			case LEFT_ROTATE:
				return intValue((leftShort << rightShort) | (leftShort >>> (short) -rightShort));
			case RIGHT_ROTATE:
				return intValue((leftShort >>> rightShort) | (leftShort << (short) -rightShort));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value<?> natNatBinaryOp(ASTNode<?> node, NatValue left, @NonNull BinaryOpType opType, NatValue right) {
		short leftShort = left.shortValue(node), rightShort = right.shortValue(node);
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
				throw undefinedBinaryOp(node, left.typeInfo, opType, right.typeInfo);
			case EQUAL_TO:
				return boolValue(leftShort == rightShort);
			case NOT_EQUAL_TO:
				return boolValue(leftShort != rightShort);
			case LESS_THAN:
				return boolValue(Helpers.shortCompareUnsigned(leftShort, rightShort) < 0);
			case LESS_OR_EQUAL:
				return boolValue(Helpers.shortCompareUnsigned(leftShort, rightShort) <= 0);
			case MORE_THAN:
				return boolValue(Helpers.shortCompareUnsigned(leftShort, rightShort) > 0);
			case MORE_OR_EQUAL:
				return boolValue(Helpers.shortCompareUnsigned(leftShort, rightShort) >= 0);
			case PLUS:
				return natValue(leftShort + rightShort);
			case AND:
				return natValue(leftShort & rightShort);
			case OR:
				return natValue(leftShort | rightShort);
			case XOR:
				return natValue(leftShort ^ rightShort);
			case MINUS:
				return natValue(leftShort - rightShort);
			case MULTIPLY:
				return natValue(leftShort * rightShort);
			case DIVIDE:
				if (rightShort == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return natValue(Helpers.shortDivideUnsigned(leftShort, rightShort));
			case REMAINDER:
				if (rightShort == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return natValue(Helpers.shortRemainderUnsigned(leftShort, rightShort));
			case LEFT_SHIFT:
				return natValue(leftShort << rightShort);
			case RIGHT_SHIFT:
				return natValue(leftShort >>> rightShort);
			case LEFT_ROTATE:
				return natValue((leftShort << rightShort) | (leftShort >>> (short) -rightShort));
			case RIGHT_ROTATE:
				return natValue((leftShort >>> rightShort) | (leftShort << (short) -rightShort));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value<?> intUnaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull IntValue value) {
		short shortValue = value.shortValue(node);
		switch (opType) {
			case MINUS:
				return intValue(-shortValue);
			case NOT:
				return intValue(~shortValue);
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value<?> natUnaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull NatValue value) {
		short shortValue = value.shortValue(node);
		switch (opType) {
			case MINUS:
				throw undefinedUnaryOp(node, opType, value.typeInfo);
			case NOT:
				return natValue(~shortValue);
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	@Override
	public void addressToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void boolToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void wordToAddressCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	@Override
	public void wordToCharCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addBinaryOpAction(node, intTypeInfo, BinaryOpType.AND, intTypeInfo, target, arg, intValue(0xFF).dataId());
	}
	
	@Override
	public void charToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	public void generateInternal() {
		if (!code.generate()) {
			code = new RedstoneCode(true);
			code.generate();
		}
	}
}
