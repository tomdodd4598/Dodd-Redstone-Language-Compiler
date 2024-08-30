package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionMultiply extends InstructionALU {
	
	public InstructionMultiply(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.MUL, RedstoneMnemonics.MULL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.MUL, RedstoneMnemonics.MULL);
	}
}
