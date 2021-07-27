package drlc.generate.drc1.instruction.jump;

import drlc.generate.drc1.instruction.Instruction;

public abstract class InstructionConditionalJump extends InstructionJump {
	
	public InstructionConditionalJump(short section) {
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
