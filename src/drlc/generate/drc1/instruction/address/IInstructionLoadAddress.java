package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.instruction.IInstructionLoad;

public interface IInstructionLoadAddress extends IInstructionLoad, IInstructionAddress {
	
	@Override
	public default DataInfo getDataInfo() {
		return getLoadedData();
	}
	
	public DataInfo getLoadedData();
}
