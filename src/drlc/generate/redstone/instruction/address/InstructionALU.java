package drlc.generate.redstone.instruction.address;

import drlc.generate.redstone.DataInfo;

public abstract class InstructionALU extends InstructionAddress {
	
	public InstructionALU(DataInfo info) {
		super(info);
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
