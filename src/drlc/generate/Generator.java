package drlc.generate;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

import drlc.generate.intermediate.IntermediateGenerator;
import drlc.generate.intermediate.IntermediateOptimization;
import drlc.generate.redstone.RedstoneAssemblyGenerator;
import drlc.generate.redstone.RedstoneBinaryGenerator;
import drlc.generate.redstone.RedstoneHexadecimalGenerator;
import drlc.generate.redstone.RedstoneOCGenerator;
import drlc.interpret.Program;
import drlc.interpret.routine.Routine;
import drlc.node.Node;

public abstract class Generator {
	
	public static final Map<String, Class<? extends Generator>> MAP = new LinkedHashMap<>();
	
	static {
		MAP.put("i", IntermediateGenerator.class);
		MAP.put("s", RedstoneAssemblyGenerator.class);
		MAP.put("b", RedstoneBinaryGenerator.class);
		MAP.put("h", RedstoneHexadecimalGenerator.class);
		MAP.put("oc", RedstoneOCGenerator.class);
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
		Map<String, Routine> map = program.getRoutineMap();
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
