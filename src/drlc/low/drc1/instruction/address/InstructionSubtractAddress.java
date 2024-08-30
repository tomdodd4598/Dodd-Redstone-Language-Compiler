package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionSubtractAddress extends InstructionALU {
	
	public InstructionSubtractAddress(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.SUBI, RedstoneMnemonics.SUBLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.SUBI, RedstoneMnemonics.SUBLI);
	}
}
