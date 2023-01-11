package drlc.low.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionOrOffset extends InstructionALUOffset {
	
	public InstructionOrOffset(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionOrOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.ORNB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.ORPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.ORNB.concat("\t").concat(Helpers.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.ORPB.concat("\t").concat(Helpers.toHex(offset, 2));
		}
	}
}
