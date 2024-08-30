package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionRightShiftOffset extends InstructionALUOffset {
	
	public InstructionRightShiftOffset(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.RSHPB, RedstoneMnemonics.RSHNB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.RSHPB, RedstoneMnemonics.RSHNB);
	}
}
