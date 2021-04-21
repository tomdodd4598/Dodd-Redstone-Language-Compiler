package drlc.generate.drc1.instruction.immediate;

import drlc.generate.drc1.instruction.Instruction;

public abstract class InstructionLongImmediate extends Instruction implements IInstructionLongImmediate {
	
	public final short value;
	
	public InstructionLongImmediate(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public boolean precedesData() {
		return true;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		return null;
	}
}
