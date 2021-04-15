package drlc.generate.redstone.instruction.address;

import drlc.Helper;
import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.RedstoneRoutine;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionAnd extends InstructionALU {
	
	public InstructionAnd(DataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionAnd(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.AND).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.AND.concat("\t").concat(Helper.toHex(address, 2));
	}
}
