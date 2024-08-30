package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionDivideOffset extends InstructionALUOffset {
	
	public InstructionDivideOffset(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.DIVPB, RedstoneMnemonics.DIVNB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.DIVPB, RedstoneMnemonics.DIVNB);
	}
}
