package drlc.low.drc1.instruction.address;

import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public interface IInstructionAddress {
	
	public RedstoneDataInfo getDataInfo();
	
	public boolean isDataFromMemory();
	
	public boolean isDataToMemory();
	
	public Instruction getDataReplacement(RedstoneCode code);
	
	public default RedstoneDataInfo getDataInfoReplacement(RedstoneCode code) {
		RedstoneDataInfo info = getDataInfo();
		return code.getRoutine(info.routineName).dataInfo(info.argId);
	}
}
