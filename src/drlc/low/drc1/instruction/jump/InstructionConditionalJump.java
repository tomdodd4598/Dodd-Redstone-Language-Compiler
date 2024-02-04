package drlc.low.drc1.instruction.jump;

import drlc.low.drc1.instruction.Instruction;

public abstract class InstructionConditionalJump extends InstructionJump {
	
	public InstructionConditionalJump(int section) {
		super(section);
	}
	
	@Override
	public abstract boolean isCurrentRegisterValueUsed();
	
	@Override
	public boolean isDefiniteJump() {
		return false;
	}
	
	public abstract Instruction getReplacementConditionalJump(Instruction previous);
	
	@Override
	public abstract String binaryString();
	
	@Override
	public abstract String toString();
}
