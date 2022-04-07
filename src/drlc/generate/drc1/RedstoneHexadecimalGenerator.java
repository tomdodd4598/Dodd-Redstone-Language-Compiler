package drlc.generate.drc1;

import java.io.PrintWriter;
import java.util.List;

import drlc.Helpers;
import drlc.generate.drc1.instruction.Instruction;
import drlc.interpret.Program;

public class RedstoneHexadecimalGenerator extends RedstoneGenerator {
	
	public RedstoneHexadecimalGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate(Program program, StringBuilder builder) {
		optimizeIntermediate(program);
		program.finalizeRoutines();
		
		RedstoneCode code = generateCode(program);
		
		for (RedstoneRoutine routine : code.getRoutineMap().values()) {
			for (List<Instruction> section : routine.textSectionMap.values()) {
				for (Instruction instruction : section) {
					builder.append(Helpers.toHex(Integer.parseUnsignedInt(instruction.binaryString(), 2), 4)).append("\n");
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
