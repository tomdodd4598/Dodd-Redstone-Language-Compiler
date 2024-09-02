package drlc.low.drc1.instruction.data;

import drlc.Helpers;
import drlc.low.LowDataInfo;
import drlc.low.instruction.IInstructionAddressData;

public class InstructionAddressData extends InstructionData implements IInstructionAddressData {
	
	public LowDataInfo dataInfo;
	public Short address;
	
	public InstructionAddressData(LowDataInfo dataInfo) {
		super();
		this.dataInfo = dataInfo;
	}
	
	@Override
	public void regenerateDataInfo() {
		dataInfo = dataInfo.getRegeneratedDataInfo();
	}
	
	@Override
	public int size(boolean longAddress) {
		return 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {Helpers.toBinary(address, 16)};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return Helpers.toHex(address, longAddress ? 4 : 2);
	}
}
