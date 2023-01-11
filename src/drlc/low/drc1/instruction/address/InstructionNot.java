package drlc.low.drc1.instruction.address;

import drlc.Helpers;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionNot extends InstructionALU {
	
	public InstructionNot(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionNot(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.NOT).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.NOT.concat("\t").concat(Helpers.toHex(address, 2));
	}
}
