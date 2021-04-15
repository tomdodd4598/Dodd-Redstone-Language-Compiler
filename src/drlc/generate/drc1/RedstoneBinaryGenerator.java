package drlc.generate.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.generate.drc1.instruction.Instruction;
import drlc.interpret.Program;

public class RedstoneBinaryGenerator extends RedstoneGenerator {
	
	public RedstoneBinaryGenerator(Boolean intermediateOptimization, Boolean machineOptimization, String outputFile) {
		super(intermediateOptimization, machineOptimization, outputFile);
	}
	
	@Override
	public void generate(Program program, StringBuilder builder) {
		if (intermediateOptimization) {
			optimizeIntermediate(program);
		}
		program.finalizeRoutines();
		
		RedstoneCode code = generateCode(program);
		
		for (RedstoneRoutine routine : code.routineMap.values()) {
			for (List<Instruction> section : routine.textSectionMap.values()) {
				for (Instruction instruction : section) {
					builder.append(instruction.binaryString()).append("\n");
				}
			}
		}
		
		try {
			PrintWriter out = new PrintWriter(outputFile);
			out.print(builder.toString());
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
