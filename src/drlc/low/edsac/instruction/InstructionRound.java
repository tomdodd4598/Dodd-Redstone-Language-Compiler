package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionRound extends InstructionImmediate {
	
	public InstructionRound() {
		super(0L);
	}
	
	@Override
	protected String opcode() {
		return EdsacOpcodes.ROUND;
	}
}
