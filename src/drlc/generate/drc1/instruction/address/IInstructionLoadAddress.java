package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.instruction.IInstructionLoad;

public interface IInstructionLoadAddress extends IInstructionLoad, IInstructionAddress {
	
	@Override
	public default DataInfo getDataInfo() {
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
	
	public DataInfo getLoadedData();
}
