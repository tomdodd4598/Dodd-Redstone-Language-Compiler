package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;

public class InstructionNoOp extends Instruction {
	
	public InstructionNoOp() {
		super();
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toAssembly() {
		return EdsacOpcodes.NO_OP + EdsacOpcodes.SHORT;
	}
}
