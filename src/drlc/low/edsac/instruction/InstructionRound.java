package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionRound extends InstructionImmediate {
	
	public InstructionRound() {
		super(0L);
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.ROUND;
	}
}
