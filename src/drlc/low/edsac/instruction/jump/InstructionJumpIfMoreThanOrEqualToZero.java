package drlc.low.edsac.instruction.jump;

import drlc.low.edsac.*;

public class InstructionJumpIfMoreThanOrEqualToZero extends InstructionJump {
	
	public InstructionJumpIfMoreThanOrEqualToZero(int section) {
		super(section);
	}
	
	@Override
	public String toAssembly() {
		return EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO + EdsacCode.addressBits(address) + EdsacOpcodes.SHORT;
	}
}
