package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.instruction.address.IInstructionLoadAddress;

public class InstructionLoadA extends InstructionAddress implements IInstructionLoadAddress {
	
	public InstructionLoadA(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public LowDataInfo getLoadedData() {
		return dataInfo;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.LDA, RedstoneMnemonics.LDAL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LDA, RedstoneMnemonics.LDAL);
	}
}
