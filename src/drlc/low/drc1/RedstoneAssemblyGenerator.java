package drlc.low.drc1;

import java.util.List;

import drlc.Helpers;
import drlc.low.drc1.instruction.*;

public class RedstoneAssemblyGenerator extends RedstoneGenerator {
	
	public RedstoneAssemblyGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate() {
		RedstoneCode code = generateCode();
		
		StringBuilder sb = new StringBuilder();
		boolean begin = true;
		int i = 0;
		for (RedstoneRoutine routine : code.getRoutineMap().values()) {
			if (begin) {
				begin = false;
			}
			else {
				sb.append('\n');
			}
			sb.append(routine.name).append(":\n");
			for (List<Instruction> section : routine.textSectionMap.values()) {
				for (Instruction instruction : section) {
					appendInstruction(sb, instruction, i++);
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
	
	protected void appendInstruction(StringBuilder sb, Instruction instruction, int address) {
		if (!(instruction instanceof InstructionConstant)) {
			sb.append(String.format("%-4s", Helpers.toHex(address, 2))).append('\t').append(instruction).append('\n');
		}
	}
}
