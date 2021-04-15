package drlc.generate.drc1.instruction.address;

import drlc.Helper;
import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.RedstoneRoutine;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionRightShift extends InstructionALU {
	
	public InstructionRightShift(DataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionRightShift(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.RSH).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.RSH.concat("\t").concat(Helper.toHex(address, 2));
	}
}
