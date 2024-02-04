package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneCode;
import drlc.low.drc1.instruction.Instruction;

public interface IInstructionAddress {
	
	public LowDataInfo getDataInfo();
	
	public boolean isDataFromMemory();
	
	public boolean isDataToMemory();
	
	public Instruction getDataReplacement(RedstoneCode code);
	
	public default LowDataInfo getDataInfoReplacement(RedstoneCode code) {
		LowDataInfo info = getDataInfo();
		return code.getRoutine(info.function).dataInfo(info.argId, info.extraOffset);
	}
}
