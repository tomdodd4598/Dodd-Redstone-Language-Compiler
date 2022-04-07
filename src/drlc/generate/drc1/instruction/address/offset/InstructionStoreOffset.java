package drlc.generate.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.address.IInstructionStoreAddress;

public class InstructionStoreOffset extends InstructionAddressOffset implements IInstructionStoreAddress {
	
	public InstructionStoreOffset(RedstoneDataInfo info) {
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
		return new InstructionStoreOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.STANB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.STAPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.STANB.concat("\t").concat(Helpers.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.STAPB.concat("\t").concat(Helpers.toHex(offset, 2));
		}
	}
}
