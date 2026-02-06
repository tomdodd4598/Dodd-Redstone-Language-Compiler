package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionHalt extends InstructionImmediate {
	
	public InstructionHalt() {
		super(0L);
	}
	
	@Override
	protected String opcode() {
		return EdsacOpcodes.HALT;
	}
}
