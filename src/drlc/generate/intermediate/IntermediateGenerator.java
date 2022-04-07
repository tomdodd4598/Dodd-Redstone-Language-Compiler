package drlc.generate.intermediate;

import java.io.PrintWriter;
import java.util.List;

import drlc.*;
import drlc.generate.Generator;
import drlc.interpret.Program;
import drlc.interpret.action.Action;
import drlc.interpret.component.*;
import drlc.interpret.component.info.*;
import drlc.interpret.component.info.type.*;
import drlc.interpret.routine.*;
import drlc.node.Node;

public class IntermediateGenerator extends Generator {
	
	public IntermediateGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	protected void getDirectives() {
		directiveSet.addAll(Global.DIRECTIVES);
	}
	
	@Override
	public void handleDirectiveCall(Node node, Program program, String name, List<EvaluationInfo> infoList) {}
	
	@Override
	public void addBuiltInConstants(Node node, Program program) {
		super.addBuiltInConstants(node, program);
	}
	
	@Override
	public void addBuiltInVariables(Node node, Program program) {
		super.addBuiltInVariables(node, program);
		program.rootScope.addVariable(node, new Variable(Global.ARGC, new VariableModifierInfo(true, false, false), Global.INT_TYPE_INFO));
	}
	
	@Override
	protected void getBuiltInFunctions() {
		builtInFunctionMap.putAll(Global.BUILT_IN_FUNCTIONS);
	}
	
	@Override
	public long castInteger(long value) {
		return value;
	}
	
	@Override
	public long binaryOp(Node node, BinaryOpType opType, long value, long other) {
		switch (opType) {
			case LOGICAL_AND:
				return value != 0 && other != 0 ? 1 : 0;
			case LOGICAL_OR:
				return value != 0 || other != 0 ? 1 : 0;
			case LOGICAL_XOR:
				return value != 0 ^ other != 0 ? 1 : 0;
			case EQUAL_TO:
				return value == other ? 1 : 0;
			case NOT_EQUAL_TO:
				return value != other ? 1 : 0;
			case LESS_THAN:
				return value < other ? 1 : 0;
			case LESS_OR_EQUAL:
				return value <= other ? 1 : 0;
			case MORE_THAN:
				return value > other ? 1 : 0;
			case MORE_OR_EQUAL:
				return value >= other ? 1 : 0;
			case PLUS:
				return value + other;
			case AND:
				return value & other;
			case OR:
				return value | other;
			case XOR:
				return value ^ other;
			case MINUS:
				return value - other;
			case ARITHMETIC_LEFT_SHIFT:
				return value << other;
			case ARITHMETIC_RIGHT_SHIFT:
				return value >> other;
			case LOGICAL_RIGHT_SHIFT:
				return value >>> other;
			case CIRCULAR_LEFT_SHIFT:
				return Long.rotateLeft(value, (int) other);
			case CIRCULAR_RIGHT_SHIFT:
				return Long.rotateRight(value, (int) other);
			case MULTIPLY:
				return value * other;
			case DIVIDE:
				return value / other;
			case REMAINDER:
				return value % other;
			default:
				throw new IllegalArgumentException(String.format("Attempted to evaluate an expression including a binary op of unknown type! %s", node));
		}
	}
	
	@Override
	public long unaryOp(Node node, UnaryOpType opType, long value) {
		switch (opType) {
			case PLUS:
				return +value;
			case MINUS:
				return -value;
			case COMPLEMENT:
				return ~value;
			case TO_BOOL:
				return value == 0 ? 0 : 1;
			case NOT:
				return value == 0 ? 1 : 0;
			default:
				throw new IllegalArgumentException(String.format("Attempted to evaluate an expression including a unary op of unknown type! %s", node));
		}
	}
	
	@Override
	public int getAddressSize() {
		return 1;
	}
	
	@Override
	public void generateRootParams(RootRoutine routine) {
		routine.params = new DeclaratorInfo[2];
		routine.params[0] = new DeclaratorInfo(null, new Variable(Global.ARGC, new VariableModifierInfo(true, false, false), Global.INT_TYPE_INFO), 0);
		TypeInfo argvTypeInfo = new BasicTypeInfo(null, Global.INT_TYPE, 2);
		routine.params[1] = new DeclaratorInfo(null, new Variable("\\argv", new VariableModifierInfo(true, false, false), argvTypeInfo), 0);
	}
	
	@Override
	public void generate(Program program, StringBuilder builder) {
		optimizeIntermediate(program);
		program.finalizeRoutines();
		
		for (Routine routine : program.getRoutineMap().values()) {
			if (!routine.isBuiltInFunctionRoutine()) {
				builder.append('\n').append(routine.getType().toString()).append(' ').append(routine.toString()).append(":\n");
				List<List<Action>> list = routine.getBodyActionLists();
				for (int i = 0; i < list.size(); i++) {
					builder.append(Helpers.sectionIdString(i)).append(":\n");
					for (Action action : list.get(i)) {
						builder.append('\t').append(action.toString()).append('\n');
					}
				}
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(outputFile);
			out.print(builder.substring(1));
			out.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
