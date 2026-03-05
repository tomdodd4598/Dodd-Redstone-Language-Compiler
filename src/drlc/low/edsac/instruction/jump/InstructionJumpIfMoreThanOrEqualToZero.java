package drlc.low.edsac.instruction.jump;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionJumpIfMoreThanOrEqualToZero extends InstructionConditionalJump {
	
	public InstructionJumpIfMoreThanOrEqualToZero(int section) {
		super(section);
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO;
	}
}
