package drlc.low.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.low.LowDataInfo;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLeftShiftOffset extends InstructionALUOffset {
	
	public InstructionLeftShiftOffset(LowDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionLeftShiftOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.LSHNB) + Helpers.toBinary(-offset, 8);
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.LSHPB) + Helpers.toBinary(offset, 8);
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.LSHNB + '\t' + Helpers.toHex(-offset, 2);
		}
		else {
			return RedstoneMnemonics.LSHPB + '\t' + Helpers.toHex(offset, 2);
		}
	}
}
