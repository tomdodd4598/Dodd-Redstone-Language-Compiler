package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.DataInfo;

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
