package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionLeftShiftOffset extends InstructionALUOffset {
	
	public InstructionLeftShiftOffset(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.LSHPB, RedstoneMnemonics.LSHNB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LSHPB, RedstoneMnemonics.LSHNB);
	}
}
