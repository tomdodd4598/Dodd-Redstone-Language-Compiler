package drlc.generate.drc1.instruction.address;

import drlc.Helper;
import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.RedstoneRoutine;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLoadB extends InstructionAddress {
	
	public InstructionLoadB(DataInfo info) {
		super(info);
	}
	
	@Override
	public boolean isRegisterModified() {
		return false;
	}
	
	@Override
	public boolean isRegisterExported() {
		return false;
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionLoadB(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDB).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDB.concat("\t").concat(Helper.toHex(address, 2));
	}
}
