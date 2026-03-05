package drlc.low.drc1.instruction.address;

import drlc.*;
import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneOpcodes;
import drlc.low.drc1.instruction.Instruction;

public abstract class InstructionAddress extends Instruction {
	
	public LowDataInfo dataInfo;
	public Short address;
	public boolean longAddress = false;
	
	public InstructionAddress(LowDataInfo dataInfo) {
		super();
		this.dataInfo = dataInfo;
	}
	
	@Override
	public LowDataInfo getDataInfo() {
		return dataInfo;
	}
	
	@Override
	public void regenerateDataInfo() {
		dataInfo = dataInfo.getRegeneratedDataInfo();
	}
	
	@Override
	public boolean isUnknownMemoryAccess() {
		return false;
	}
	
	@Override
	public boolean isLoadStoreBarrier() {
		return false;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public abstract boolean isDataFromMemory();
	
	@Override
	public abstract boolean isDataToMemory();
	
	@Override
	public int size(boolean longAddress) {
		return longAddress && this.longAddress ? 2 : 1;
	}
	
	protected String[] toBinary(boolean longAddress, String mnemonic, String longMnemonic) {
		if (longAddress && this.longAddress) {
			return new String[] {RedstoneOpcodes.get(longMnemonic) + Global.ZERO_8, Helpers.toBinary(address, 16)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(mnemonic) + Helpers.toBinary(address, 8)};
		}
	}
	
	protected String toAssembly(boolean longAddress, String mnemonic, String longMnemonic) {
		String hex = Helpers.toHex(address, longAddress ? 4 : 2);
		if (longAddress && this.longAddress) {
			return longMnemonic + '\t' + hex;
		}
		else {
			return mnemonic + '\t' + hex;
		}
	}
}
