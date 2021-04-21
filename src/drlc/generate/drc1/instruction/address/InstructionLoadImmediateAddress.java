package drlc.generate.drc1.instruction.address;

import drlc.Helper;
import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.RedstoneRoutine;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLoadImmediateAddress extends InstructionAddress {
	
	public InstructionLoadImmediateAddress(DataInfo info) {
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
		return new InstructionLoadImmediateAddress(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDAI).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDAI.concat("\t").concat(Helper.toHex(address, 2));
	}
}
