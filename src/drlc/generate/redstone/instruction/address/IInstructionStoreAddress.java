package drlc.generate.redstone.instruction.address;

import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.instruction.IInstructionStore;

public interface IInstructionStoreAddress extends IInstructionStore, IInstructionAddress {
	
	@Override
	public default DataInfo getDataInfo() {
		return getStoredData();
	}
	
	public DataInfo getStoredData();
}
