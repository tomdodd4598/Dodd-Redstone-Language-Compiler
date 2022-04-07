package drlc.generate.drc1;

import java.util.List;

import drlc.*;
import drlc.generate.Generator;
import drlc.interpret.Program;
import drlc.interpret.component.*;
import drlc.interpret.component.info.*;
import drlc.interpret.routine.RootRoutine;
import drlc.node.Node;

public abstract class RedstoneGenerator extends Generator {
	
	public RedstoneGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	protected void getDirectives() {
		Helpers.subSet(directiveSet, Global.DIRECTIVES, Global.SETARGC);
	}
	
	@Override
	public void handleDirectiveCall(Node node, Program program, String name, List<EvaluationInfo> infoList) {
		int listSize = infoList.size();
		if (name.equals(Global.SETARGC)) {
			if (listSize == 1) {
				program.rootScope.addConstant(node, new Constant(Global.ARGC, Global.INT_TYPE_INFO, infoList.get(0).value), true);
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
	public void addBuiltInConstants(Node node, Program program) {
		super.addBuiltInConstants(node, program);
		program.rootScope.addConstant(node, new Constant(Global.ARGC, Global.INT_TYPE_INFO, 0), false);
	}
	
	@Override
	public void addBuiltInVariables(Node node, Program program) {
		super.addBuiltInVariables(node, program);
	}
	
	@Override
	protected void getBuiltInFunctions() {
		Helpers.subMap(builtInFunctionMap, Global.BUILT_IN_FUNCTIONS, Global.OUTCHAR, Global.OUTINT, Global.ARGV);
	}
	
	@Override
	public long castInteger(long value) {
		return (short) value;
	}
	
	@Override
	public long binaryOp(Node node, BinaryOpType opType, long value, long other) {
		short val = (short) value, oth = (short) other;
		switch (opType) {
			case LOGICAL_AND:
				return val != 0 && oth != 0 ? 1 : 0;
			case LOGICAL_OR:
				return val != 0 || oth != 0 ? 1 : 0;
			case LOGICAL_XOR:
				return val != 0 ^ oth != 0 ? 1 : 0;
			case EQUAL_TO:
				return val == oth ? 1 : 0;
			case NOT_EQUAL_TO:
				return val != oth ? 1 : 0;
			case LESS_THAN:
				return val < oth ? 1 : 0;
			case LESS_OR_EQUAL:
				return val <= oth ? 1 : 0;
			case MORE_THAN:
				return val > oth ? 1 : 0;
			case MORE_OR_EQUAL:
				return val >= oth ? 1 : 0;
			case PLUS:
				return val + oth;
			case AND:
				return val & oth;
			case OR:
				return val | oth;
			case XOR:
				return val ^ oth;
			case MINUS:
				return val - oth;
			case ARITHMETIC_LEFT_SHIFT:
				return val << (oth & 0xF);
			case ARITHMETIC_RIGHT_SHIFT:
				return val >> (oth & 0xF);
			case LOGICAL_RIGHT_SHIFT:
				return (val & 0xFFFF) >>> (oth & 0xF);
			case CIRCULAR_LEFT_SHIFT:
				return (val << oth) | ((val & 0xFFFF) >>> ((16 - oth) & 0xF));
			case CIRCULAR_RIGHT_SHIFT:
				return ((val & 0xFFFF) >>> oth) | (val << ((16 - oth) & 0xF));
			case MULTIPLY:
				return val * oth;
			case DIVIDE:
				return val / oth;
			case REMAINDER:
				return val % oth;
			default:
				throw new IllegalArgumentException(String.format("Attempted to evaluate an expression including a binary op of unknown type! %s", node));
		}
	}
	
	@Override
	public long unaryOp(Node node, UnaryOpType opType, long value) {
		short val = (short) value;
		switch (opType) {
			case PLUS:
				return (short) +val;
			case MINUS:
				return (short) -val;
			case COMPLEMENT:
				return (short) ~val;
			case TO_BOOL:
				return val == 0 ? 0 : 1;
			case NOT:
				return val == 0 ? 1 : 0;
			default:
				throw new IllegalArgumentException(String.format("Attempted to evaluate an expression including a unary op of unknown type! %s", node));
		}
	}
	
	@Override
	public int getAddressSize() {
		return 1;
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
	
	public static DataId rootParamDataId(int rootParamIndex) {
		return new DataId(rootParamString(rootParamIndex), Global.ROOT_SCOPE_ID);
	}
	
	@Override
	public void generateRootParams(RootRoutine routine) {
		int argc = (int) routine.program.rootScope.getConstant(null, Global.ARGC).value;
		routine.params = new DeclaratorInfo[argc];
		for (int i = 0; i < argc; i++) {
			routine.params[i] = new DeclaratorInfo(null, new Variable(rootParamString(i), new VariableModifierInfo(true, false, true), Global.INT_TYPE_INFO), 0);
		}
	}
	
	public RedstoneCode generateCode(Program program) {
		RedstoneCode code = new RedstoneCode(program);
		code.generate();
		return code;
	}
}
