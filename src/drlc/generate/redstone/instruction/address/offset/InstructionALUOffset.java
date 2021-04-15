package drlc.generate.redstone.instruction.address.offset;

import drlc.generate.redstone.DataInfo;

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
