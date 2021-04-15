package drlc.generate.redstone.instruction.set;

import drlc.generate.redstone.instruction.Instruction;

public abstract class InstructionSet extends Instruction {
	
	public InstructionSet() {
		super();
	}
	
	@Override
	public boolean isRegisterModified() {
		return true;
	}
	
	@Override
	public boolean isRegisterExported() {
		return false;
	}
}
