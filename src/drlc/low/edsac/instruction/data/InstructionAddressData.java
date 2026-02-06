package drlc.low.edsac.instruction.data;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacInt;
import drlc.low.instruction.IInstructionAddressData;

public class InstructionAddressData extends InstructionData implements IInstructionAddressData {
	
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
	public String toAssembly() {
		return EdsacInt.of(address).toAssembly();
	}
}
