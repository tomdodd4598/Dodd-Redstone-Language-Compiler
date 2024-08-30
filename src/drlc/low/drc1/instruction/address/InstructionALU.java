package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;

public abstract class InstructionALU extends InstructionAddress {
	
	public InstructionALU(LowDataInfo dataInfo) {
		super(dataInfo);
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
