package drlc.generate.drc1.instruction.address.offset;

import drlc.generate.drc1.DataInfo;

public abstract class InstructionALUOffset extends InstructionAddressOffset {
	
	public InstructionALUOffset(DataInfo info) {
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
