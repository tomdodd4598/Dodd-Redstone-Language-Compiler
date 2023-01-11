package drlc.low.drc1;

import java.util.List;

import drlc.*;
import drlc.intermediate.Scope;
import drlc.intermediate.component.*;
import drlc.intermediate.component.constant.*;
import drlc.intermediate.component.info.*;
import drlc.intermediate.routine.RootRoutine;
import drlc.node.Node;

public abstract class RedstoneGenerator extends Generator {
	
	public RedstoneGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void addBuiltInConstants(Node node) {
		super.addBuiltInConstants(node);
		program.rootScope.addConstant(node, new Constant(Global.ARGC, intTypeInfo, 0), false);
	}
	
	@Override
	public void handleDirectiveCall(Node node, String name, List<Constant> constantList) {
		int listSize = constantList.size();
		if (name.equals(Global.SETARGC)) {
			if (listSize == 1) {
				program.rootScope.addConstant(node, new Constant(Global.ARGC, intTypeInfo, infoList.get(0).value), true);
			}
			else {
				throw new IllegalArgumentException(String.format("Directive \"%s\" requires 1 argument but received %d! %s", name, listSize, node));
			}
		}
		else if (directiveSet.contains(name)) {
			throw new IllegalArgumentException(String.format("Unexpectedly encountered unimplemented directive \"%s\"! %s", name, node));
		}
		else {
			throw new IllegalArgumentException(String.format("Encountered undefined directive \"%s\"! %s", name, node));
		}
	}
	
	@Override
	public Constant intIntBinaryOp(Node node, Scope scope, IntConstant left, BinaryOpType opType, LongConstant<?, ?> right) {
		short leftShort = left.value.shortValue(), rightShort = right.value.shortValue();
		switch (opType) {
			case EQUAL_TO:
				return boolConstant(leftShort == rightShort);
			case NOT_EQUAL_TO:
				return boolConstant(leftShort != rightShort);
			case LESS_THAN:
				return boolConstant(leftShort < rightShort);
			case LESS_OR_EQUAL:
				return boolConstant(leftShort <= rightShort);
			case MORE_THAN:
				return boolConstant(leftShort > rightShort);
			case MORE_OR_EQUAL:
				return boolConstant(leftShort >= rightShort);
			case PLUS:
				return intConstant((short) (leftShort + rightShort));
			case AND:
				return intConstant((short) (leftShort & rightShort));
			case OR:
				return intConstant((short) (leftShort | rightShort));
			case XOR:
				return intConstant((short) (leftShort ^ rightShort));
			case MINUS:
				return intConstant((short) (leftShort - rightShort));
			case LEFT_SHIFT:
				return intConstant((short) (leftShort << (rightShort & 0xF)));
			case RIGHT_SHIFT:
				return intConstant((short) (leftShort >> (rightShort & 0xF)));
			case LEFT_ROTATE:
				return intConstant((short) ((short) (leftShort << rightShort) | (short) (leftShort >>> ((16 - rightShort) & 0xF))));
			case RIGHT_ROTATE:
				return intConstant((short) ((short) (leftShort >>> rightShort) | (short) (leftShort << ((16 - rightShort) & 0xF))));
			case MULTIPLY:
				return intConstant((short) (leftShort * rightShort));
			case DIVIDE:
				return intConstant((short) (leftShort / rightShort));
			case REMAINDER:
				return intConstant((short) (leftShort % rightShort));
			default:
				throw unknownBinaryOpType(node, opType, left.typeInfo, right.typeInfo);
		}
	}
	
	@Override
	public Constant natNatBinaryOp(Node node, Scope scope, NatConstant left, BinaryOpType opType, LongConstant<?, ?> right) {
		short leftShort = left.value.shortValue(), rightShort = right.value.shortValue();
		switch (opType) {
			case EQUAL_TO:
				return boolConstant(leftShort == rightShort);
			case NOT_EQUAL_TO:
				return boolConstant(leftShort != rightShort);
			case LESS_THAN:
				return boolConstant(Helpers.shortCompareUnsigned(leftShort, rightShort) < 0);
			case LESS_OR_EQUAL:
				return boolConstant(Helpers.shortCompareUnsigned(leftShort, rightShort) <= 0);
			case MORE_THAN:
				return boolConstant(Helpers.shortCompareUnsigned(leftShort, rightShort) > 0);
			case MORE_OR_EQUAL:
				return boolConstant(Helpers.shortCompareUnsigned(leftShort, rightShort) >= 0);
			case PLUS:
				return intConstant((short) (leftShort + rightShort));
			case AND:
				return intConstant((short) (leftShort & rightShort));
			case OR:
				return intConstant((short) (leftShort | rightShort));
			case XOR:
				return intConstant((short) (leftShort ^ rightShort));
			case MINUS:
				return intConstant((short) (leftShort - rightShort));
			case LEFT_SHIFT:
				return natConstant((short) (leftShort << (rightShort & 0xF)));
			case RIGHT_SHIFT:
				return natConstant((short) (leftShort >>> (rightShort & 0xF)));
			case LEFT_ROTATE:
				return natConstant((short) ((short) (leftShort << rightShort) | (short) (leftShort >>> ((16 - rightShort) & 0xF))));
			case RIGHT_ROTATE:
				return natConstant((short) ((short) (leftShort >>> rightShort) | (short) (leftShort << ((16 - rightShort) & 0xF))));
			case MULTIPLY:
				return natConstant((short) (leftShort * rightShort));
			case DIVIDE:
				return natConstant(Helpers.shortDivideUnsigned(leftShort, rightShort));
			case REMAINDER:
				return natConstant(Helpers.shortRemainderUnsigned(leftShort, rightShort));
			default:
				throw unknownBinaryOpType(node, opType, left.typeInfo, right.typeInfo);
		}
	}
	
	@Override
	public Constant intUnaryOp(Node node, Scope scope, UnaryOpType opType, IntConstant constant) {
		short shortValue = constant.value.shortValue();
		switch (opType) {
			case MINUS:
				return intConstant((short) -shortValue);
			case NOT:
				return intConstant((short) ~shortValue);
			default:
				throw unknownUnaryOpType(node, opType, constant.typeInfo);
		}
	}
	
	@Override
	public Constant natUnaryOp(Node node, Scope scope, UnaryOpType opType, NatConstant constant) {
		short shortValue = constant.value.shortValue();
		switch (opType) {
			case MINUS:
				throw undefinedUnaryOp(node, opType, constant.typeInfo);
			case NOT:
				return natConstant((short) ~shortValue);
			default:
				throw unknownUnaryOpType(node, opType, constant.typeInfo);
		}
	}
	
	@Override
	public int getWordSize() {
		return 1;
	}
	
	@Override
	public int getAddressSize() {
		return getWordSize();
	}
	
	public static final String ROOT_PARAM_PREFIX = "\\argv";
	
	public static String rootParamString(int rootParamIndex) {
		return ROOT_PARAM_PREFIX.concat(Integer.toString(rootParamIndex));
	}
	
	public static boolean isRootParam(String rootParamString) {
		return rootParamString.startsWith(ROOT_PARAM_PREFIX);
	}
	
	public static Integer parseRootParam(String rootParamString) {
		if (isRootParam(rootParamString)) {
			return Integer.parseInt(rootParamString.substring(ROOT_PARAM_PREFIX.length()));
		}
		else {
			return null;
		}
	}
	
	public DataId rootParamDataId(int rootParamIndex) {
		return new DataId(rootParamString(rootParamIndex), program.rootScope);
	}
	
	@Override
	public void generateRootParams(RootRoutine routine) {
		int argc = (int) routine.generator.program.rootScope.getConstant(null, Global.ARGC).value;
		routine.params = new DeclaratorInfo[argc];
		for (int i = 0; i < argc; ++i) {
			routine.params[i] = new DeclaratorInfo(null, new Variable(rootParamString(i), new VariableModifierInfo(true), intTypeInfo));
		}
	}
	
	public RedstoneCode generateCode() {
		RedstoneCode code = new RedstoneCode(this);
		code.generate();
		return code;
	}
}
