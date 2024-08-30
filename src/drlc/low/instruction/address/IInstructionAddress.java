package drlc.low.instruction.address;

import drlc.low.LowDataInfo;

public interface IInstructionAddress {
	
	public LowDataInfo getDataInfo();
	
	public boolean isDataFromMemory();
	
	public boolean isDataToMemory();
	
	public void regenerateDataInfo();
}
