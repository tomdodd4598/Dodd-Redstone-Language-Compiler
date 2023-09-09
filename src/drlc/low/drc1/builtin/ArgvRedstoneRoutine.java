package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.component.data.DataId;
import drlc.low.drc1.RedstoneCode;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.pointer.InstructionDereferenceA;
import drlc.low.drc1.instruction.set.InstructionSetNot;
import drlc.low.drc1.instruction.subroutine.InstructionReturnFromSubroutine;

public class ArgvRedstoneRoutine extends BuiltInRedstoneRoutine {
	
	public ArgvRedstoneRoutine(RedstoneCode code, String name) {
		super(code, name);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		textSectionMap.put((short) 0, text);
		
		DataId index = params[0].dataId();
		
		load(text, index);
		text.add(new InstructionSetNot());
		text.add(new InstructionDereferenceA());
		if (!isStackRoutine()) {
			text.add(new InstructionReturnFromSubroutine());
		}
	}
	
	@Override
	public short getFinalTextSectionKey() {
		return Short.MAX_VALUE;
	}
}
