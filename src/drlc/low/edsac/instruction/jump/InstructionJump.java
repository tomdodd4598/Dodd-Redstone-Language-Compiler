package drlc.low.edsac.instruction.jump;

import drlc.low.edsac.instruction.Instruction;

public abstract class InstructionJump extends Instruction {
	
	public final int section;
	public Integer address;
	
	public InstructionJump(int section) {
		super();
		this.section = section;
	}
	
	@Override
	public int size() {
		return 1;
	}
}
