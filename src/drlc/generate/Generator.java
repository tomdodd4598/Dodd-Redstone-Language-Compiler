package drlc.generate;

import java.util.*;

import drlc.generate.intermediate.*;
import drlc.interpret.Program;
import drlc.interpret.routine.Routine;
import drlc.node.Node;

public abstract class Generator {
	
	public static final Map<String, Class<? extends Generator>> CLASS_MAP = new LinkedHashMap<>();
	public static final Map<String, String> NAME_MAP = new HashMap<>();
	
	static {
		put("i", IntermediateGenerator.class, "Intermediate");
		put("s1", drlc.generate.drc1.RedstoneAssemblyGenerator.class, "DRC1 Assembly");
		put("b1", drlc.generate.drc1.RedstoneBinaryGenerator.class, "DRC1 Binary");
		put("h1", drlc.generate.drc1.RedstoneHexadecimalGenerator.class, "DRC1 Hexadecimal");
		put("oc1", drlc.generate.drc1.RedstoneOCGenerator.class, "DRC1 OC Input");
	}
	
	private static void put(String id, Class<? extends Generator> clazz, String name) {
		CLASS_MAP.put(id, clazz);
		NAME_MAP.put(id, name);
	}
	
	protected final boolean intermediateOptimization, machineOptimization;
	protected final String outputFile;
	
	public Generator(Boolean intermediateOptimization, Boolean machineOptimization, String outputFile) {
		this.intermediateOptimization = intermediateOptimization;
		this.machineOptimization = machineOptimization;
		this.outputFile = outputFile;
	}
	
	public abstract void checkInteger(Node node, int value);
	
	public abstract int inverse(int value);
	
	public abstract void generate(Program program, StringBuilder builder);
	
	public void optimizeIntermediate(Program program) {
		Map<String, Routine> map = program.routineMap;
		for (String name : new HashSet<>(map.keySet())) {
			if (!map.get(name).called) {
				map.remove(name);
			}
		}
		for (Routine routine : map.values()) {
			boolean flag = true;
			while (flag) {
				flag = IntermediateOptimization.removeDeadActions(routine);
				flag |= IntermediateOptimization.simplifySections(routine);
				flag |= IntermediateOptimization.simplifyJumps(routine);
				flag |= IntermediateOptimization.shiftActions(routine);
				flag |= IntermediateOptimization.replaceJumps(routine);
				flag |= IntermediateOptimization.compressRValueRegisters(routine);
				flag |= IntermediateOptimization.compressLValueRegisters(routine);
				flag |= IntermediateOptimization.reorderRValues(routine);
				flag |= IntermediateOptimization.removeNoOps(routine);
				flag |= IntermediateOptimization.orderRegisters(routine);
			}
		}
	}
}
