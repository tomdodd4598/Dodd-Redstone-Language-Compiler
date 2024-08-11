package drlc.low.drc1;

import java.util.List;

import drlc.Helpers;
import drlc.low.drc1.instruction.Instruction;

public class RedstoneAssemblyGenerator extends RedstoneGenerator {
	
	public RedstoneAssemblyGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate() {
		generateInternal();
		
		StringBuilder sb = new StringBuilder();
		boolean begin = true;
		int address = 0;
		for (RedstoneRoutine routine : code.routineMap.values()) {
			if (begin) {
				begin = false;
			}
			else {
				sb.append('\n');
			}
			sb.append(routine.function.asmString()).append(":\n");
			for (List<Instruction> section : routine.textSectionMap.values()) {
				for (Instruction instruction : section) {
					appendInstruction(sb, instruction, address, code.longAddress);
					address += instruction.size(code.longAddress);
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
	
	protected void appendInstruction(StringBuilder sb, Instruction instruction, int address, boolean longAddress) {
		sb.append(String.format("%-4s", Helpers.toHex(address, longAddress ? 4 : 2))).append('\t').append(instruction.toAssembly(longAddress)).append('\n');
	}
}
