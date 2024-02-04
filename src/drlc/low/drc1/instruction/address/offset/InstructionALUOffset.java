package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;

public abstract class InstructionALUOffset extends InstructionAddressOffset {
	
	public InstructionALUOffset(LowDataInfo info) {
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
