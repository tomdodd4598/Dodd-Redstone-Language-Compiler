package drlc.generate.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionSubtractOffset extends InstructionALUOffset {
	
	public InstructionSubtractOffset(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionSubtractOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.SUBNB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.SUBPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.SUBNB.concat("\t").concat(Helpers.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.SUBPB.concat("\t").concat(Helpers.toHex(offset, 2));
		}
	}
}
