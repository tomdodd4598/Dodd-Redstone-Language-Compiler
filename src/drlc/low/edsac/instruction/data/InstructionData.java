package drlc.low.edsac.instruction.data;

import drlc.low.edsac.instruction.Instruction;

public abstract class InstructionData extends Instruction {
	
	protected InstructionData() {
		super();
	}
	
	@Override
	public abstract int size();
}
