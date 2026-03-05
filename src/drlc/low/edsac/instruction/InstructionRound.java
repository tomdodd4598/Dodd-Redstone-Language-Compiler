package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionRound extends InstructionImmediate {
	
	public InstructionRound() {
		super(0L);
	}

	@Override
	public boolean isAccumulatorUsed() {
		return true;
	}

	@Override
	public boolean isAccumulatorModified() {
		return true;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.ROUND;
	}
}
