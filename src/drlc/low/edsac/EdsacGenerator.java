package drlc.low.edsac;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;

public abstract class EdsacGenerator extends Generator {
	
	protected EdsacCode code = new EdsacCode();
	
	public EdsacGenerator(String outputFile) {
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
		return intValue(EdsacInt.of(value));
	}
	
	@Override
	public @NonNull NatValue natValue(long value) {
		return natValue(EdsacInt.of(value));
	}
	
	public @NonNull IntValue intValue(EdsacInt value) {
		return new IntValue(null, value.toSigned());
	}
	
	public @NonNull NatValue natValue(EdsacInt value) {
		return new NatValue(null, value.toLong());
	}
	
	@Override
	public String getStringLiteral(ASTNode<?> node, String raw) {
		StringBuilder sb = new StringBuilder();
		Boolean currentShift = null;
		for (byte b : raw.getBytes()) {
			EdsacChar edsacChar = EdsacChar.of((char) b);
			if (edsacChar.requiresLetterShift()) {
				if (currentShift == null || currentShift) {
					sb.append(EdsacChar.LETTER_SHIFT.code);
				}
				currentShift = false;
			}
			else if (edsacChar.requiresFigureShift()) {
				if (currentShift == null || !currentShift) {
					sb.append(EdsacChar.FIGURE_SHIFT.code);
				}
				currentShift = true;
			}
			sb.append(edsacChar.code);
		}
		return sb.toString();
	}
	
	@Override
	public int getWordSize() {
		return 2;
	}
	
	@Override
	public int getFunctionSize() {
		return 1;
	}
	
	@Override
	public int getAddressSize() {
		return 1;
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
		EdsacInt leftInt = EdsacInt.of(left.value), rightInt = EdsacInt.of(right.value);
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
				throw undefinedBinaryOp(node, left.typeInfo, opType, right.typeInfo);
			case EQUAL_TO:
				return boolValue(leftInt.equals(rightInt));
			case NOT_EQUAL_TO:
				return boolValue(!leftInt.equals(rightInt));
			case LESS_THAN:
				return boolValue(leftInt.compare(rightInt) < 0);
			case LESS_OR_EQUAL:
				return boolValue(leftInt.compare(rightInt) <= 0);
			case MORE_THAN:
				return boolValue(leftInt.compare(rightInt) > 0);
			case MORE_OR_EQUAL:
				return boolValue(leftInt.compare(rightInt) >= 0);
			case PLUS:
				return intValue(leftInt.plus(rightInt));
			case AND:
				return intValue(leftInt.and(rightInt));
			case OR:
				return intValue(leftInt.or(rightInt));
			case XOR:
				return intValue(leftInt.xor(rightInt));
			case MINUS:
				return intValue(leftInt.minus(rightInt));
			case MULTIPLY:
				return intValue(leftInt.multiply(rightInt));
			case DIVIDE:
				if (rightInt.toSigned() == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return intValue(leftInt.divide(rightInt));
			case REMAINDER:
				if (rightInt.toSigned() == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return intValue(leftInt.remainder(rightInt));
			case LEFT_SHIFT:
				return intValue(leftInt.leftShift(rightInt));
			case RIGHT_SHIFT:
				return intValue(leftInt.rightShift(rightInt));
			case LEFT_ROTATE:
				return intValue(leftInt.leftShift(rightInt).or(leftInt.rightShiftUnsigned(rightInt.minus())));
			case RIGHT_ROTATE:
				return intValue(leftInt.rightShiftUnsigned(rightInt).or(leftInt.leftShift(rightInt.minus())));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value<?> natNatBinaryOp(ASTNode<?> node, NatValue left, @NonNull BinaryOpType opType, NatValue right) {
		EdsacInt leftInt = EdsacInt.of(left.value), rightInt = EdsacInt.of(right.value);
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
				throw undefinedBinaryOp(node, left.typeInfo, opType, right.typeInfo);
			case EQUAL_TO:
				return boolValue(leftInt.equals(rightInt));
			case NOT_EQUAL_TO:
				return boolValue(!leftInt.equals(rightInt));
			case LESS_THAN:
				return boolValue(leftInt.compareUnsigned(rightInt) < 0);
			case LESS_OR_EQUAL:
				return boolValue(leftInt.compareUnsigned(rightInt) <= 0);
			case MORE_THAN:
				return boolValue(leftInt.compareUnsigned(rightInt) > 0);
			case MORE_OR_EQUAL:
				return boolValue(leftInt.compareUnsigned(rightInt) >= 0);
			case PLUS:
				return natValue(leftInt.plus(rightInt));
			case AND:
				return natValue(leftInt.and(rightInt));
			case OR:
				return natValue(leftInt.or(rightInt));
			case XOR:
				return natValue(leftInt.xor(rightInt));
			case MINUS:
				return natValue(leftInt.minus(rightInt));
			case MULTIPLY:
				return natValue(leftInt.multiply(rightInt));
			case DIVIDE:
				if (rightInt.toLong() == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return natValue(leftInt.divideUnsigned(rightInt));
			case REMAINDER:
				if (rightInt.toLong() == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return natValue(leftInt.remainderUnsigned(rightInt));
			case LEFT_SHIFT:
				return natValue(leftInt.leftShift(rightInt));
			case RIGHT_SHIFT:
				return natValue(leftInt.rightShiftUnsigned(rightInt));
			case LEFT_ROTATE:
				return natValue(leftInt.leftShift(rightInt).or(leftInt.rightShiftUnsigned(rightInt.minus())));
			case RIGHT_ROTATE:
				return natValue(leftInt.rightShiftUnsigned(rightInt).or(leftInt.leftShift(rightInt.minus())));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value<?> intUnaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull IntValue value) {
		EdsacInt intValue = EdsacInt.of(value.value);
		switch (opType) {
			case MINUS:
				return intValue(intValue.minus());
			case NOT:
				return intValue(intValue.not());
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value<?> natUnaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull NatValue value) {
		EdsacInt intValue = EdsacInt.of(value.value);
		switch (opType) {
			case MINUS:
				throw undefinedUnaryOp(node, opType, value.typeInfo);
			case NOT:
				return natValue(intValue.not());
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	@Override
	public long wordToAddressCast(ASTNode<?> node, long valueLong) {
		return EdsacInt.of(valueLong).lowBits().toLong();
	}
	
	@Override
	public int wordToCharCast(ASTNode<?> node, long valueLong) {
		return EdsacInt.of(valueLong).toChar();
	}
	
	@Override
	public long charToWordCast(ASTNode<?> node, byte valueByte) {
		return EdsacInt.fromChar(valueByte).toLong();
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
		routine.addBinaryOpAction(node, intTypeInfo, BinaryOpType.AND, intTypeInfo, target, arg, intValue(EdsacInt.SHORT_MASK).dataId());
	}
	
	@Override
	public void wordToCharCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addBinaryOpAction(node, intTypeInfo, BinaryOpType.AND, intTypeInfo, target, arg, intValue(EdsacInt.CHAR_MASK).dataId());
	}
	
	@Override
	public void charToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg) {
		routine.addAssignmentAction(node, target, arg);
	}
	
	public void generateInternal() {
		code.generate();
	}
}
