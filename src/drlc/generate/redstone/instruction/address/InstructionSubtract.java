package drlc.generate.redstone.instruction.address;

import drlc.Helper;
import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.RedstoneRoutine;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionSubtract extends InstructionALU {
	
	public InstructionSubtract(DataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionSubtract(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.SUB).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.SUB.concat("\t").concat(Helper.toHex(address, 2));
	}
}
