package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionRemainderOffset extends InstructionALUOffset {
	
	public InstructionRemainderOffset(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.REMPB, RedstoneMnemonics.REMNB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.REMPB, RedstoneMnemonics.REMNB);
	}
}
