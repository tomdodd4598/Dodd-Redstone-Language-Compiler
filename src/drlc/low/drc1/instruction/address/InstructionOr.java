package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionOr extends InstructionALU {
	
	public InstructionOr(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.OR, RedstoneMnemonics.ORL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.OR, RedstoneMnemonics.ORL);
	}
}
