package drlc.low.drc1;

import java.util.List;
import java.util.function.Consumer;

import drlc.*;
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
		
		int[] address = {0};
		int hexLength = code.longAddress ? 4 : 2;
		
		Consumer<Instruction> appendInstruction = x -> {
			int size = x.size(code.longAddress);
			if (size > 0) {
				sb.append(String.format("%-4s", Helpers.toHex(address[0], hexLength))).append('\t').append(x.toAssembly(code.longAddress)).append('\n');
				address[0] += size;
			}
		};
		
		for (RedstoneRoutine routine : code.routineMap.values()) {
			if (begin) {
				begin = false;
			}
			else {
				sb.append('\n');
			}
			sb.append(routine.function.asmString()).append(":\n");
			for (List<Instruction> section : routine.sectionTextMap.values()) {
				section.stream().forEach(appendInstruction);
			}
		}
		
		if (!code.staticDataMap.isEmpty()) {
			sb.append('\n').append(Global.DATA).append(":\n");
			code.staticDataMap.values().stream().forEach(appendInstruction);
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
