package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;

public class InstructionAddStackTargetOffset extends InstructionAdd {
	
	public LowDataInfo stackTargetDataInfo;
	
	public InstructionAddStackTargetOffset(LowDataInfo stackTargetDataInfo) {
		super(stackTargetDataInfo);
		this.stackTargetDataInfo = stackTargetDataInfo;
	}
	
	@Override
	public LowDataInfo getReadDataInfo() {
		return null;
	}
	
	@Override
	public void regenerateDataInfo() {
		stackTargetDataInfo = stackTargetDataInfo.getRegeneratedDataInfo();
		if (dataInfo != stackTargetDataInfo) {
			dataInfo = dataInfo.getRegeneratedDataInfo();
		}
		else {
			dataInfo = stackTargetDataInfo;
		}
	}
}
