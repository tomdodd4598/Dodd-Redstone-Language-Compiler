package drlc.generate.drc1.instruction.address.offset;

import drlc.Helper;
import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.RedstoneRoutine;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLoadImmediateAddressOffset extends InstructionAddressOffset {
	
	public InstructionLoadImmediateAddressOffset(DataInfo info) {
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
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionLoadImmediateAddressOffset(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDINB).concat(Helper.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDIPB).concat(Helper.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.LDINB.concat("\t").concat(Helper.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.LDIPB.concat("\t").concat(Helper.toHex(offset, 2));
		}
	}
}
