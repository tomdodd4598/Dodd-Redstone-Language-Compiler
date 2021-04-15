package drlc.generate.redstone.instruction.address;

import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.instruction.IInstructionLoad;

public interface IInstructionLoadAddress extends IInstructionLoad, IInstructionAddress {
	
	@Override
	public default DataInfo getDataInfo() {
		return getLoadedData();
	}
	
	public DataInfo getLoadedData();
}
