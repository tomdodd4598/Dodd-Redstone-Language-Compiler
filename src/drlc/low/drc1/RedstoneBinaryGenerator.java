package drlc.low.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.low.drc1.instruction.Instruction;

public class RedstoneBinaryGenerator extends RedstoneGenerator {
	
	public RedstoneBinaryGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate() {
		optimizeIntermediate();
		program.finalizeRoutines();
		
		RedstoneCode code = generateCode();
		
		StringBuilder builder = new StringBuilder();
		for (RedstoneRoutine routine : code.getRoutineMap().values()) {
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
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
