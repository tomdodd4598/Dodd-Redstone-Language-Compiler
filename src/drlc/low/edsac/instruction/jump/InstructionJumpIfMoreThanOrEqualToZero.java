package drlc.low.edsac.instruction.jump;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionJumpIfMoreThanOrEqualToZero extends InstructionJump {
	
	public InstructionJumpIfMoreThanOrEqualToZero(int section) {
		super(section);
	}
	
	@Override
	protected String opcode() {
		return EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO;
	}
}
