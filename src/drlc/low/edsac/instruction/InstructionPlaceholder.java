package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionPlaceholder extends InstructionImmediate {
	
	public InstructionPlaceholder() {
		super(0L);
	}
	
	@Override
	public boolean isLoadStoreBarrier() {
		return true;
	}
	
	@Override
	public boolean isProtected() {
		return true;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.NO_OP;
	}
}
