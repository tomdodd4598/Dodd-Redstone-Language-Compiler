package drlc.low.edsac.instruction.jump;

import drlc.low.edsac.*;

public class InstructionJumpIfLessThanZero extends InstructionJump {
	
	public InstructionJumpIfLessThanZero(int section) {
		super(section);
	}
	
	@Override
	public String toAssembly() {
		return EdsacOpcodes.JUMP_IF_LESS_THAN_ZERO + EdsacCode.addressBits(address) + EdsacOpcodes.SHORT;
	}
}
