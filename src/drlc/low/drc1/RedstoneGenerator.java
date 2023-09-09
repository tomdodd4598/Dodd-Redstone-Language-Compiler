package drlc.low.drc1;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.RootRoutine;

public abstract class RedstoneGenerator extends Generator {
	
	public RedstoneGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInDirectives(ASTNode node) {
		directiveMap.put(Global.SETARGC, new Directive(1, Helpers.array(Helpers.builtInParam("x", intTypeInfo))) {
			
			@Override
			public void call(Value[] values) {
				program.rootScope.addConstant(node, new Constant(Global.ARGC, values[0]), true);
			}
		});
	}
	
	@Override
	public void addBuiltInConstants(ASTNode node) {
		super.addBuiltInConstants(node);
		program.rootScope.addConstant(node, new Constant(Global.ARGC, intValue(0)), false);
	}
	
	@Override
	public @NonNull Value intIntBinaryOp(ASTNode node, IntValue left, @NonNull BinaryOpType opType, WordValue right) {
		short leftShort = left.shortValue(node), rightShort = right.shortValue(node);
		switch (opType) {
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
			case LEFT_SHIFT:
				return intValue(leftShort << rightShort);
			case RIGHT_SHIFT:
				return intValue(leftShort >> rightShort);
			case LEFT_ROTATE:
				return intValue((leftShort << rightShort) | (leftShort >>> (short) -rightShort));
			case RIGHT_ROTATE:
				return intValue((leftShort >>> rightShort) | (leftShort << (short) -rightShort));
			case MULTIPLY:
				return intValue(leftShort * rightShort);
			case DIVIDE:
				return intValue(leftShort / rightShort);
			case REMAINDER:
				return intValue(leftShort % rightShort);
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value natNatBinaryOp(ASTNode node, NatValue left, @NonNull BinaryOpType opType, WordValue right) {
		short leftShort = left.shortValue(node), rightShort = right.shortValue(node);
		switch (opType) {
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
			case LEFT_SHIFT:
				return natValue(leftShort << rightShort);
			case RIGHT_SHIFT:
				return natValue(leftShort >>> rightShort);
			case LEFT_ROTATE:
				return natValue((leftShort << rightShort) | (leftShort >>> (short) -rightShort));
			case RIGHT_ROTATE:
				return natValue((leftShort >>> rightShort) | (leftShort << (short) -rightShort));
			case MULTIPLY:
				return natValue(leftShort * rightShort);
			case DIVIDE:
				return natValue(Helpers.shortDivideUnsigned(leftShort, rightShort));
			case REMAINDER:
				return natValue(Helpers.shortRemainderUnsigned(leftShort, rightShort));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	@Override
	public @NonNull Value intUnaryOp(ASTNode node, @NonNull UnaryOpType opType, @NonNull IntValue value) {
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
	public @NonNull Value natUnaryOp(ASTNode node, @NonNull UnaryOpType opType, @NonNull NatValue value) {
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
	
	public static String rootParamString(int rootParamIndex) {
		return Global.ARGV_PARAM + rootParamIndex;
	}
	
	public static boolean isRootParam(String rootParamString) {
		return rootParamString.startsWith(Global.ARGV_PARAM);
	}
	
	public static Integer parseRootParam(String rootParamString) {
		if (isRootParam(rootParamString)) {
			return Integer.parseInt(rootParamString.substring(Global.ARGV_PARAM.length()));
		}
		else {
			return null;
		}
	}
	
	public DataId rootParamDataId(RootRoutine routine, int rootParamIndex) {
		return new VariableDataId(0, routine.params[rootParamIndex].variable);
	}
	
	@Override
	public void generateRootParams(RootRoutine routine) {
		int argc = program.rootScope.getConstant(null, Global.ARGC).value.intValue(null);
		routine.params = new DeclaratorInfo[argc];
		for (int i = 0; i < argc; ++i) {
			routine.params[i] = new DeclaratorInfo(null, new Variable(rootParamString(i), VariableModifier.ROOT_PARAM, intTypeInfo));
		}
	}
	
	public RedstoneCode generateCode() {
		RedstoneCode code = new RedstoneCode(this, program);
		code.generate();
		return code;
	}
}
