package drlc.generate.drc1.builtin;

import java.util.*;

import drlc.generate.drc1.RedstoneCode;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.pointer.InstructionDereferenceA;
import drlc.generate.drc1.instruction.set.InstructionSetNot;
import drlc.generate.drc1.instruction.subroutine.InstructionReturnFromSubroutine;
import drlc.interpret.component.DataId;

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
