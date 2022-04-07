package drlc.generate.drc1.instruction.address;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionMultiply extends InstructionALU {
	
	public InstructionMultiply(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionMultiply(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.MUL).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.MUL.concat("\t").concat(Helpers.toHex(address, 2));
	}
}
