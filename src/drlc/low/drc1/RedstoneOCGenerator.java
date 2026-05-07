package drlc.low.drc1;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

import drlc.Helpers;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.data.InstructionValueData;

public class RedstoneOCGenerator extends RedstoneGenerator {
	
	public RedstoneOCGenerator(String outputFile) {
		super(outputFile);
	}
	
	@Override
	public void generate() throws IOException {
		generateInternal();
		
		StringBuilder sb = new StringBuilder();
		boolean[] begin = {true};
		int[] address = {0};
		
		Consumer<Instruction> appendInstruction = x -> {
			for (String binary : x.toBinary(code.mixedWidth)) {
				if (begin[0]) {
					begin[0] = false;
				}
				else {
					sb.append(' ');
				}
				sb.append(Integer.parseUnsignedInt(binary, 2));
			}
			address[0] += x.size(code.mixedWidth);
		};
		Instruction zeroData = new InstructionValueData(Arrays.asList((short) 0));
		Consumer<Integer> appendPaddingTo = target -> {
			while (address[0] < target) {
				appendInstruction.accept(zeroData);
			}
			if (address[0] > target) {
				throw new IllegalStateException(String.format("DRC1 OC emission address %s exceeded routine start address %s!", address[0], target));
			}
		};
		
		Runnable appendData = () -> code.getStaticDataInAddressOrder(!code.textFirst).stream().forEach(appendInstruction);
		
		if (code.textFirst) {
			for (RedstoneRoutine routine : code.routineMap.values()) {
				appendPaddingTo.accept(code.textAddressMap.get(routine.function));
				for (List<Instruction> section : routine.sectionTextMap.values()) {
					section.forEach(appendInstruction);
				}
			}
			appendData.run();
		}
		else {
			for (RedstoneRoutine routine : code.routineMap.values()) {
				if (routine.isRootRoutine()) {
					appendPaddingTo.accept(code.textAddressMap.get(routine.function));
					for (List<Instruction> section : routine.sectionTextMap.values()) {
						section.forEach(appendInstruction);
					}
				}
			}
			appendData.run();
			for (RedstoneRoutine routine : code.routineMap.values()) {
				if (!routine.isRootRoutine()) {
					appendPaddingTo.accept(code.textAddressMap.get(routine.function));
					for (List<Instruction> section : routine.sectionTextMap.values()) {
						section.forEach(appendInstruction);
					}
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
