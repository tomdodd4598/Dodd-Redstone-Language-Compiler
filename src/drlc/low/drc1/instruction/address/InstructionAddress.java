package drlc.low.drc1.instruction.address;

import drlc.*;
import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneOpcodes;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.instruction.address.IInstructionAddress;

public abstract class InstructionAddress extends Instruction implements IInstructionAddress {
	
	public LowDataInfo dataInfo;
	public Short address;
	
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
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public int size(boolean longAddress) {
		return longAddress ? 2 : 1;
	}
	
	protected String[] toBinary(boolean longAddress, String mnemonic, String longMnemonic) {
		if (longAddress) {
			return new String[] {RedstoneOpcodes.get(longMnemonic) + Global.ZERO_8, Helpers.toBinary(address, 16)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(mnemonic) + Helpers.toBinary(address, 8)};
		}
	}
	
	protected String toAssembly(boolean longAddress, String mnemonic, String longMnemonic) {
		if (longAddress) {
			return longMnemonic + '\t' + Helpers.toHex(address, 4);
		}
		else {
			return mnemonic + '\t' + Helpers.toHex(address, 2);
		}
	}
}
