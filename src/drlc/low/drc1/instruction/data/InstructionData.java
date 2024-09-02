package drlc.low.drc1.instruction.data;

import drlc.low.drc1.instruction.Instruction;

public abstract class InstructionData extends Instruction {
	
	public InstructionData() {
		super();
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
}
