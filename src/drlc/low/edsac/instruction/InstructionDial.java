package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionDial extends InstructionImmediate {
	
	public InstructionDial() {
		super(0L);
	}
	
	@Override
	public boolean isLoadStoreBarrier() {
		return true;
	}
	
	@Override
	public boolean isUnknownMemoryAccess() {
		return true;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.HALT;
	}
}
