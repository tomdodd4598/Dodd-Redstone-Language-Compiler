package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;

public class InstructionLoadBOffset extends InstructionAddressOffset {
	
	public InstructionLoadBOffset(LowDataInfo dataInfo) {
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
		return toBinary(longAddress, RedstoneMnemonics.LDBPB, RedstoneMnemonics.LDBNB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LDBPB, RedstoneMnemonics.LDBNB);
	}
}
