package drlc.generate.redstone.instruction.address;

import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.RedstoneRoutine;
import drlc.generate.redstone.instruction.Instruction;

public interface IInstructionAddress {
	
	public DataInfo getDataInfo();
	
	public Instruction getDataReplacement(RedstoneRoutine routine);
}
