package drlc.low.drc1.instruction.address;

import drlc.low.drc1.RedstoneDataInfo;
import drlc.low.drc1.instruction.IInstructionLoad;

public interface IInstructionLoadAddress extends IInstructionLoad, IInstructionAddress {
	
	@Override
	public default RedstoneDataInfo getDataInfo() {
		return getLoadedData();
	}
	
	@Override
	public default boolean isDataFromMemory() {
		return true;
	}
	
	@Override
	public default boolean isDataToMemory() {
		return false;
	}
	
	public RedstoneDataInfo getLoadedData();
}
