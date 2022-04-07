package drlc.generate.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionAndOffset extends InstructionALUOffset {
	
	public InstructionAndOffset(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionAndOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.ANDNB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.ANDPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.ANDNB.concat("\t").concat(Helpers.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.ANDPB.concat("\t").concat(Helpers.toHex(offset, 2));
		}
	}
}
