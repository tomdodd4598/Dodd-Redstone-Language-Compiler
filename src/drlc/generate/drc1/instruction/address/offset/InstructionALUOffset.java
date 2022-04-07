package drlc.generate.drc1.instruction.address.offset;

import drlc.generate.drc1.RedstoneDataInfo;

public abstract class InstructionALUOffset extends InstructionAddressOffset {
	
	public InstructionALUOffset(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public boolean isDataFromMemory() {
		return true;
	}
	
	@Override
	public boolean isDataToMemory() {
		return false;
	}
}
