package drlc.low.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.instruction.IInstructionStore;

public interface IInstructionStoreAddress extends IInstructionStore, IInstructionAddress {
	
	@Override
	public default LowDataInfo getDataInfo() {
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
	
	public LowDataInfo getStoredData();
}
