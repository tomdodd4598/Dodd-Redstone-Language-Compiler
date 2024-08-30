package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.instruction.address.IInstructionLoadAddress;

public class InstructionLoadAOffset extends InstructionAddressOffset implements IInstructionLoadAddress {
	
	public InstructionLoadAOffset(LowDataInfo dataInfo) {
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
		return toBinary(longAddress, RedstoneMnemonics.LDAPB, RedstoneMnemonics.LDANB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LDAPB, RedstoneMnemonics.LDANB);
	}
}
