package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.instruction.address.IInstructionStoreAddress;

public class InstructionStoreOffset extends InstructionAddressOffset implements IInstructionStoreAddress {
	
	public InstructionStoreOffset(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return true;
	}
	
	@Override
	public LowDataInfo getStoredData() {
		return dataInfo;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.STAPB, RedstoneMnemonics.STANB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.STAPB, RedstoneMnemonics.STANB);
	}
}
