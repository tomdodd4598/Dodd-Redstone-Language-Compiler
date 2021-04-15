package drlc.generate.drc1.instruction.jump;

import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.set.InstructionSet;

public abstract class InstructionConditionalJump extends InstructionJump {
	
	public InstructionConditionalJump(short section) {
		super(section);
	}
	
	public abstract Instruction getReplacementConditionalJump(InstructionSet instructionSet);
	
	@Override
	public abstract String binaryString();
	
	@Override
	public abstract String toString();
}
