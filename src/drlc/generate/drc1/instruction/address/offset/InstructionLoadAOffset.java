package drlc.generate.drc1.instruction.address.offset;

import drlc.Helper;
import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.RedstoneRoutine;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.address.IInstructionLoadAddress;

public class InstructionLoadAOffset extends InstructionAddressOffset implements IInstructionLoadAddress {
	
	public InstructionLoadAOffset(DataInfo info) {
		super(info);
	}
	
	@Override
	public boolean isRegisterModified() {
		return true;
	}
	
	@Override
	public boolean isRegisterExported() {
		return false;
	}
	
	@Override
	public DataInfo getLoadedData() {
		return info;
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionLoadAOffset(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDANB).concat(Helper.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDAPB).concat(Helper.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.LDANB.concat("\t").concat(Helper.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.LDAPB.concat("\t").concat(Helper.toHex(offset, 2));
		}
	}
}
