package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionLoadB extends InstructionAddress {
	
	public InstructionLoadB(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public boolean isDataFromMemory() {
		return true;
	}
	
	@Override
	public boolean isDataToMemory() {
		return false;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.LDB, RedstoneMnemonics.LDBL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LDB, RedstoneMnemonics.LDBL);
	}
}
