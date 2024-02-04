package drlc.low.drc1;

import java.util.List;

import drlc.Helpers;
import drlc.low.drc1.instruction.Instruction;

public class RedstoneOCGenerator extends RedstoneGenerator {
	
	public RedstoneOCGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate() {
		RedstoneCode code = generateCode();
		
		StringBuilder sb = new StringBuilder();
		boolean begin = true;
		for (RedstoneRoutine routine : code.routineMap.values()) {
			for (List<Instruction> section : routine.textSectionMap.values()) {
				for (Instruction instruction : section) {
					if (begin) {
						begin = false;
					}
					else {
						sb.append(" ");
					}
					sb.append(Integer.parseUnsignedInt(instruction.binaryString(), 2));
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
