package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.component.DataId;
import drlc.low.drc1.RedstoneCode;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.immediate.InstructionAndImmediate;
import drlc.low.drc1.instruction.subroutine.InstructionReturnFromSubroutine;

public class OutCharRedstoneRoutine extends BuiltInRedstoneRoutine {
	
	public OutCharRedstoneRoutine(RedstoneCode code, String name) {
		super(code, name);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		textSectionMap.put((short) 0, text);
		
		DataId c = params[0].dataId();
		
		load(text, c);
		text.add(new InstructionAndImmediate((short) 0x7F));
		text.add(new InstructionOutput());
		if (!isStackRoutine()) {
			text.add(new InstructionReturnFromSubroutine());
		}
	}
	
	@Override
	public short getFinalTextSectionKey() {
		return Short.MAX_VALUE;
	}
}
