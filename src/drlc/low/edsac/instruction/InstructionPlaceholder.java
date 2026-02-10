package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionPlaceholder extends InstructionImmediate {
	
	public InstructionPlaceholder() {
		super(0L);
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.NO_OP;
	}
}
