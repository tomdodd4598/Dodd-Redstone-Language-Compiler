package drlc.generate.redstone.instruction.jump;

import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.set.InstructionSet;

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
