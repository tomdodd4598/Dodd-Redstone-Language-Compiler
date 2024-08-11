package drlc.low.drc1.instruction.set;

import drlc.low.drc1.instruction.Instruction;

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
	
	@Override
	public int size(boolean longAddress) {
		return 1;
	}
}
