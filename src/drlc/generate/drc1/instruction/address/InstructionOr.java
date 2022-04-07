package drlc.generate.drc1.instruction.address;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionOr extends InstructionALU {
	
	public InstructionOr(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionOr(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.OR).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.OR.concat("\t").concat(Helpers.toHex(address, 2));
	}
}
