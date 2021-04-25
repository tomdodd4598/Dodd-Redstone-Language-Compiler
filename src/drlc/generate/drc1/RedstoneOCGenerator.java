package drlc.generate.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.generate.drc1.instruction.Instruction;
import drlc.interpret.Program;

public class RedstoneOCGenerator extends RedstoneGenerator {
	
	public RedstoneOCGenerator(Boolean intermediateOptimization, Boolean machineOptimization, String outputFile) {
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
					builder.append(" ").append(Integer.parseUnsignedInt(instruction.binaryString(), 2));
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
