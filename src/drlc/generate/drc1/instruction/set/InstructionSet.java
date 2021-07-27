package drlc.generate.drc1.instruction.set;

import drlc.generate.drc1.instruction.Instruction;

public abstract class InstructionSet extends Instruction {
	
	public InstructionSet() {
		super();
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
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
