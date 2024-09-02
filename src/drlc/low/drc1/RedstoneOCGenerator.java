package drlc.low.drc1;

import java.util.List;
import java.util.function.Consumer;

import drlc.Helpers;
import drlc.low.drc1.instruction.Instruction;

public class RedstoneOCGenerator extends RedstoneGenerator {
	
	public RedstoneOCGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate() {
		generateInternal();
		
		StringBuilder sb = new StringBuilder();
		boolean[] begin = {true};
		
		Consumer<Instruction> appendInstruction = x -> {
			for (String binary : x.toBinary(code.longAddress)) {
				if (begin[0]) {
					begin[0] = false;
				}
				else {
					sb.append(' ');
				}
				sb.append(Integer.parseUnsignedInt(binary, 2));
			}
		};
		
		for (RedstoneRoutine routine : code.routineMap.values()) {
			for (List<Instruction> section : routine.sectionTextMap.values()) {
				section.forEach(appendInstruction);
			}
		}
		
		if (!code.staticDataMap.isEmpty()) {
			code.staticDataMap.values().stream().forEach(appendInstruction);
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
