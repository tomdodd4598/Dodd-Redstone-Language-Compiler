package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionSubtractAddressOffset extends InstructionALUOffset {
	
	public InstructionSubtractAddressOffset(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.SUBIPB, RedstoneMnemonics.SUBINB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.SUBIPB, RedstoneMnemonics.SUBINB);
	}
}
