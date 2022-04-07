package drlc.generate;

import java.util.*;
import java.util.Map.Entry;

import drlc.Global;
import drlc.generate.intermediate.*;
import drlc.interpret.Program;
import drlc.interpret.component.*;
import drlc.interpret.component.info.EvaluationInfo;
import drlc.interpret.routine.*;
import drlc.node.Node;

public abstract class Generator {
	
	public static final Map<String, Class<? extends Generator>> CLASS_MAP = new LinkedHashMap<>();
	public static final Map<String, String> NAME_MAP = new HashMap<>();
	
	static {
		put("i", IntermediateGenerator.class, "Intermediate");
		put("s1", drlc.generate.drc1.RedstoneAssemblyGenerator.class, "DRC1 Assembly");
		// put("b1", drlc.generate.drc1.RedstoneBinaryGenerator.class, "DRC1 Binary");
		// put("h1", drlc.generate.drc1.RedstoneHexadecimalGenerator.class, "DRC1 Hexadecimal");
		put("oc1", drlc.generate.drc1.RedstoneOCGenerator.class, "DRC1 OC Input");
	}
	
	private static void put(String id, Class<? extends Generator> clazz, String name) {
		CLASS_MAP.put(id, clazz);
		NAME_MAP.put(id, name);
	}
	
	protected final String outputFile;
	
	public final Set<String> directiveSet = new HashSet<>();
	public final Map<String, Function> builtInFunctionMap = new HashMap<>();
	
	public Generator(String outputFile) {
		this.outputFile = outputFile;
		getDirectives();
	}
	
	protected abstract void getDirectives();
	
	public abstract void handleDirectiveCall(Node node, Program program, String name, List<EvaluationInfo> infoList);
	
	public void addBuiltInTypes(Node node, Program program) {
		program.rootScope.addType(node, Global.VOID_TYPE);
		program.rootScope.addType(node, Global.INT_TYPE);
		program.rootScope.addType(node, Global.fun_type = new Type(Global.FUN, getAddressSize()));
	}
	
	public void addBuiltInConstants(Node node, Program program) {}
	
	public void addBuiltInVariables(Node node, Program program) {}
	
	public void addBuiltInFunctions(Node node, Program program) {
		getBuiltInFunctions();
		for (Entry<String, Function> entry : builtInFunctionMap.entrySet()) {
			addBuiltInFunction(node, program, entry.getKey(), entry.getValue());
		}
	}
	
	protected abstract void getBuiltInFunctions();
	
	protected void addBuiltInFunction(Node node, Program program, String name, Function function) {
		program.rootScope.addFunction(node, function, false);
		Routine routine = new FunctionRoutine(node, program, name, function);
		program.getRoutineMap().put(name, routine);
		program.getBuiltInRoutineMap().put(name, routine);
	}
	
	public abstract long castInteger(long value);
	
	public abstract long binaryOp(Node node, BinaryOpType opType, long value, long other);
	
	public abstract long unaryOp(Node node, UnaryOpType opType, long value);
	
	public abstract int getAddressSize();
	
	public abstract void generateRootParams(RootRoutine routine);
	
	public abstract void generate(Program program, StringBuilder builder);
	
	public void optimizeIntermediate(Program program) {
		Map<String, Routine> map = program.getRoutineMap();
		for (String name : new HashSet<>(map.keySet())) {
			Routine routine = map.get(name);
			if (routine.isFunctionRoutine() && !routine.getFunction().required) {
				map.remove(name);
			}
		}
		for (Routine routine : map.values()) {
			boolean flag = true;
			while (flag) {
				flag = IntermediateOptimization.removeNoOps(routine);
				flag |= IntermediateOptimization.removeDeadActions(routine);
				flag |= IntermediateOptimization.simplifySections(routine);
				flag |= IntermediateOptimization.simplifyJumps(routine);
				flag |= IntermediateOptimization.shiftActions(routine);
				flag |= IntermediateOptimization.replaceJumps(routine);
				flag |= IntermediateOptimization.compressRvalueRegisters(routine);
				flag |= IntermediateOptimization.compressLvalueRegisters(routine);
				flag |= IntermediateOptimization.reorderRvalues(routine);
				flag |= IntermediateOptimization.orderRegisters(routine);
				flag |= IntermediateOptimization.simplifyAddressDereferences(routine);
			}
		}
	}
}
