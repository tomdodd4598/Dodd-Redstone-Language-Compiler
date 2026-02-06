package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionNoOp extends InstructionImmediate {
	
	public final boolean placeholder;
	
	public InstructionNoOp(boolean placeholder) {
		super(0L);
		this.placeholder = placeholder;
	}
	
	public InstructionNoOp() {
		this(false);
	}
	
	@Override
	protected String opcode() {
		return EdsacOpcodes.NO_OP;
	}
}
