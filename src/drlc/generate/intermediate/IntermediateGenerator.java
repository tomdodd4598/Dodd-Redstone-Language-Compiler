package drlc.generate.intermediate;

import java.io.PrintWriter;
import java.util.List;

import drlc.Global;
import drlc.generate.Generator;
import drlc.interpret.Program;
import drlc.interpret.action.Action;
import drlc.interpret.routine.Routine;
import drlc.node.Node;

public class IntermediateGenerator extends Generator {
	
	public IntermediateGenerator(Boolean intermediateOptimization, Boolean machineOptimization, String outputFile) {
		super(intermediateOptimization, machineOptimization, outputFile);
	}
	
	@Override
	public void checkInteger(Node node, int value) {}
	
	@Override
	public int inverse(int value) {
		return ~value;
	}
	
	@Override
	public void generate(Program program, StringBuilder builder) {
		if (intermediateOptimization) {
			optimizeIntermediate(program);
		}
		program.finalizeRoutines();
		
		for (Routine routine : program.getRoutineMap().values()) {
			builder.append("\n::[").append(routine.getType().str).append("] ").append(routine.toString()).append("::\n");
			List<List<Action>> list = routine.getBodyActionLists();
			for (int i = 0; i < list.size(); i++) {
				builder.append(Global.SECTION).append(Integer.toString(i)).append(":\n");
				for (Action action : list.get(i)) {
					builder.append("\t").append(action.toString()).append("\n");
				}
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(outputFile);
			out.print(builder.substring(1));
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
