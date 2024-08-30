package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionNot extends InstructionALU {
	
	public InstructionNot(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.NOT, RedstoneMnemonics.NOTL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.NOT, RedstoneMnemonics.NOTL);
	}
}
