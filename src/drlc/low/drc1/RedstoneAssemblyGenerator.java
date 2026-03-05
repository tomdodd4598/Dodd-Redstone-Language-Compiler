package drlc.low.drc1;

import java.util.*;
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
		boolean[] begin = {true};
		
		int[] address = {0};
		int hexLength = code.addressOffset > RedstoneCode.BYTE_MASK ? 4 : 2;
		
		Consumer<Instruction> appendInstruction = x -> {
			int size = x.size(code.mixedWidth);
			if (size > 0) {
				sb.append(String.format("%-4s", Helpers.toHex(address[0], hexLength))).append('\t').append(x.toAssembly(code.mixedWidth)).append('\n');
				address[0] += size;
			}
		};
		Consumer<Integer> appendPaddingTo = target -> {
			if (address[0] > target) {
				throw new IllegalStateException(String.format("DRC1 assembly emission address %s exceeded routine start address %s!", address[0], target));
			}
			address[0] = target;
		};
		
		Consumer<RedstoneRoutine> appendRoutine = routine -> {
			appendPaddingTo.accept(code.textAddressMap.get(routine.function));
			if (begin[0]) {
				begin[0] = false;
			}
			else {
				sb.append('\n');
			}
			sb.append(routine.function.asmString()).append(":\n");
			for (List<Instruction> section : routine.sectionTextMap.values()) {
				section.stream().forEach(appendInstruction);
			}
		};
		
		Runnable appendData = () -> {
			List<Map.Entry<Integer, Instruction>> staticDataEntries = code.getInitializedStaticDataEntriesInAddressOrder();
			if (staticDataEntries.isEmpty()) {
				return;
			}
			
			if (begin[0]) {
				begin[0] = false;
			}
			else {
				sb.append('\n');
			}
			sb.append(Global.DATA).append(":\n");
			for (Map.Entry<Integer, Instruction> entry : staticDataEntries) {
				appendPaddingTo.accept(entry.getKey());
				appendInstruction.accept(entry.getValue());
			}
		};
		
		if (code.textFirst) {
			for (RedstoneRoutine routine : code.routineMap.values()) {
				appendRoutine.accept(routine);
			}
			appendData.run();
		}
		else {
			for (RedstoneRoutine routine : code.routineMap.values()) {
				if (routine.isRootRoutine()) {
					appendRoutine.accept(routine);
				}
			}
			appendData.run();
			for (RedstoneRoutine routine : code.routineMap.values()) {
				if (!routine.isRootRoutine()) {
					appendRoutine.accept(routine);
				}
			}
		}
		
		Helpers.writeFile(outputFile, sb.toString());
	}
}
