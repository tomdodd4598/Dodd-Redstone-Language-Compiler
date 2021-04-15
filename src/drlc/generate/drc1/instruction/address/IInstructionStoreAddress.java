package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.instruction.IInstructionStore;

public interface IInstructionStoreAddress extends IInstructionStore, IInstructionAddress {
	
	@Override
	public default DataInfo getDataInfo() {
		return getStoredData();
	}
	
	public DataInfo getStoredData();
}
