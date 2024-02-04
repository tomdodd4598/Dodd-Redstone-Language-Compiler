package drlc.low.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.low.LowDataInfo;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionNotOffset extends InstructionALUOffset {
	
	public InstructionNotOffset(LowDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionNotOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.NOTNB) + Helpers.toBinary(-offset, 8);
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.NOTPB) + Helpers.toBinary(offset, 8);
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.NOTNB + '\t' + Helpers.toHex(-offset, 2);
		}
		else {
			return RedstoneMnemonics.NOTPB + '\t' + Helpers.toHex(offset, 2);
		}
	}
}
