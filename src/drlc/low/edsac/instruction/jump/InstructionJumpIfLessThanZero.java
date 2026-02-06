package drlc.low.edsac.instruction.jump;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionJumpIfLessThanZero extends InstructionJump {
	
	public InstructionJumpIfLessThanZero(int section) {
		super(section);
	}
	
	@Override
	protected String opcode() {
		return EdsacOpcodes.JUMP_IF_LESS_THAN_ZERO;
	}
}
