package drlc.low.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.Helpers;
import drlc.low.drc1.instruction.Instruction;

public class RedstoneHexadecimalGenerator extends RedstoneGenerator {
	
	public RedstoneHexadecimalGenerator(String outputFile) {
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
					builder.append(Helpers.toHex(Integer.parseUnsignedInt(instruction.binaryString(), 2), 4)).append("\n");
				}
			}
		}
		
		try (PrintWriter out = new PrintWriter(outputFile)) {
			out.print(builder.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
