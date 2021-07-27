package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public interface IInstructionAddress {
	
	public DataInfo getDataInfo();
	
	public boolean isDataFromMemory();
	
	public boolean isDataToMemory();
	
	public Instruction getDataReplacement(RedstoneRoutine routine);
}
