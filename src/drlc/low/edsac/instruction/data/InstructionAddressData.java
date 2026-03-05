package drlc.low.edsac.instruction.data;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacInt;

public class InstructionAddressData extends InstructionData {
	
	public LowDataInfo dataInfo;
	public Integer address;
	
	public InstructionAddressData(LowDataInfo dataInfo) {
		super();
		this.dataInfo = dataInfo;
	}
	
	@Override
	public void regenerateDataInfo() {
		dataInfo = dataInfo.getRegeneratedDataInfo();
	}
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return EdsacInt.of(address + (offset == null ? 0 : offset)).toAssembly();
	}
}
