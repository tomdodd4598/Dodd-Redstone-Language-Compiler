package drlc.low.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.Helpers;
import drlc.low.drc1.instruction.*;

public class RedstoneAssemblyGenerator extends RedstoneGenerator {
	
	public RedstoneAssemblyGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate() {
		optimizeIntermediate();
		program.finalizeRoutines();
		
		RedstoneCode code = generateCode();
		
		StringBuilder builder = new StringBuilder();
		int i = -1;
		for (RedstoneRoutine routine : code.getRoutineMap().values()) {
			builder.append("\n").append(routine.name).append(":\n");
			for (List<Instruction> section : routine.textSectionMap.values()) {
				for (Instruction instruction : section) {
					appendInstruction(builder, instruction, ++i);
				}
			}
		}
		
		try (PrintWriter out = new PrintWriter(outputFile)) {
			out.print(builder.substring(1));
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
