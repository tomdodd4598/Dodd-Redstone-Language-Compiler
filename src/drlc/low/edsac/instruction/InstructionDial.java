package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionDial extends InstructionImmediate {
	
	public InstructionDial() {
		super(0L);
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.HALT;
	}
}
