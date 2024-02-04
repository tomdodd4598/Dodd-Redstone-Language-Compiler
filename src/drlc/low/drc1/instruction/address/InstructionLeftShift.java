package drlc.low.drc1.instruction.address;

import drlc.Helpers;
import drlc.low.LowDataInfo;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLeftShift extends InstructionALU {
	
	public InstructionLeftShift(LowDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionLeftShift(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LSH) + Helpers.toBinary(address, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LSH + '\t' + Helpers.toHex(address, 2);
	}
}
