package drlc.low.edsac.instruction.jump;

import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public abstract class InstructionJump extends Instruction {
	
	public final int section;
	public Integer address;
	
	public InstructionJump(int section) {
		super();
		this.section = section;
	}
	
	protected abstract char opcode();
	
	@Override
	public String toAssembly(Integer offset) {
		return opcode() + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
