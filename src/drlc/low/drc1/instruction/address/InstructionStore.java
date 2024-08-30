package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.instruction.address.IInstructionStoreAddress;

public class InstructionStore extends InstructionAddress implements IInstructionStoreAddress {
	
	public InstructionStore(LowDataInfo dataInfo) {
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
		return toBinary(longAddress, RedstoneMnemonics.STA, RedstoneMnemonics.STAL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.STA, RedstoneMnemonics.STAL);
	}
}
