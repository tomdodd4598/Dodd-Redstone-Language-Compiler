package drlc.low.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.address.IInstructionLoadAddress;

public class InstructionLoadAOffset extends InstructionAddressOffset implements IInstructionLoadAddress {
	
	public InstructionLoadAOffset(RedstoneDataInfo info) {
		super(info);
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
	public RedstoneDataInfo getLoadedData() {
		return info;
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionLoadAOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDANB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDAPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.LDANB.concat("\t").concat(Helpers.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.LDAPB.concat("\t").concat(Helpers.toHex(offset, 2));
		}
	}
}
