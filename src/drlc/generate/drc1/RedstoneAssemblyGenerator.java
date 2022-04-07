package drlc.generate.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.Helpers;
import drlc.generate.drc1.instruction.*;
import drlc.interpret.Program;

public class RedstoneAssemblyGenerator extends RedstoneGenerator {
	
	public RedstoneAssemblyGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate(Program program, StringBuilder builder) {
		optimizeIntermediate(program);
		program.finalizeRoutines();
		
		RedstoneCode code = generateCode(program);
		
		int i = -1;
		for (RedstoneRoutine routine : code.getRoutineMap().values()) {
			builder.append("\n").append(routine.name).append(":\n");
			for (List<Instruction> section : routine.textSectionMap.values()) {
				for (Instruction instruction : section) {
					appendInstruction(builder, instruction, ++i);
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
	
	protected void appendInstruction(StringBuilder builder, Instruction instruction, int address) {
		if (!(instruction instanceof InstructionConstant)) {
			builder.append(String.format("%-4s", Helpers.toHex(address, 2))).append("\t").append(instruction.toString()).append("\n");
		}
	}
}
