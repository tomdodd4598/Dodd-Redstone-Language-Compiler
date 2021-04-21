package drlc.generate.drc1.instruction.immediate;

import drlc.generate.drc1.instruction.Instruction;

public abstract class InstructionImmediate extends Instruction implements IInstructionImmediate {
	
	public final short value;
	
	public InstructionImmediate(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		return null;
	}
}
