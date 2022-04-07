package drlc.generate.drc1.builtin;

import java.util.*;

import drlc.generate.drc1.RedstoneCode;
import drlc.generate.drc1.instruction.*;
import drlc.generate.drc1.instruction.subroutine.InstructionReturnFromSubroutine;
import drlc.interpret.component.DataId;

public class OutIntRedstoneRoutine extends BuiltInRedstoneRoutine {
	
	public OutIntRedstoneRoutine(RedstoneCode code, String name) {
		super(code, name);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		textSectionMap.put((short) 0, text);
		
		DataId x = params[0].dataId();
		
		load(text, x);
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
