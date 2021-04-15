package drlc.generate.drc1.instruction.set;

import drlc.generate.drc1.instruction.Instruction;

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
