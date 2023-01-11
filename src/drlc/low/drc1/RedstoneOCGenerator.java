package drlc.low.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.low.drc1.instruction.Instruction;

public class RedstoneOCGenerator extends RedstoneGenerator {
	
	public RedstoneOCGenerator(String outputFile) {
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
					builder.append(' ').append(Integer.parseUnsignedInt(instruction.binaryString(), 2));
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
