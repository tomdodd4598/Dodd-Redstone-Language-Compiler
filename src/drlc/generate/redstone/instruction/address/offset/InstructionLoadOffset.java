package drlc.generate.redstone.instruction.address.offset;

import drlc.Helper;
import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.RedstoneRoutine;
import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.address.IInstructionLoadAddress;

public class InstructionLoadOffset extends InstructionAddressOffset implements IInstructionLoadAddress {
	
	public InstructionLoadOffset(DataInfo info) {
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
		return new InstructionLoadOffset(routine.dataInfo(info.argName));
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
