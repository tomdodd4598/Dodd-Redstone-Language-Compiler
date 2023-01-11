package drlc.low.drc1.instruction.address;

import drlc.Helpers;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionStore extends InstructionAddress implements IInstructionStoreAddress {
	
	public InstructionStore(RedstoneDataInfo info) {
		super(info);
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
	public RedstoneDataInfo getStoredData() {
		return info;
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionStore(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.STA).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.STA.concat("\t").concat(Helpers.toHex(address, 2));
	}
}
