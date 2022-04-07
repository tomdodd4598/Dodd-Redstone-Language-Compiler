package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.RedstoneDataInfo;
import drlc.generate.drc1.instruction.IInstructionStore;

public interface IInstructionStoreAddress extends IInstructionStore, IInstructionAddress {
	
	@Override
	public default RedstoneDataInfo getDataInfo() {
		return getStoredData();
	}
	
	@Override
	public default boolean isDataFromMemory() {
		return false;
	}
	
	@Override
	public default boolean isDataToMemory() {
		return true;
	}
	
	public RedstoneDataInfo getStoredData();
}
