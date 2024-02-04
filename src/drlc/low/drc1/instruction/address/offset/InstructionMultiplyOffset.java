package drlc.low.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.low.LowDataInfo;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionMultiplyOffset extends InstructionALUOffset {
	
	public InstructionMultiplyOffset(LowDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionMultiplyOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.MULNB) + Helpers.toBinary(-offset, 8);
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.MULPB) + Helpers.toBinary(offset, 8);
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.MULNB + '\t' + Helpers.toHex(-offset, 2);
		}
		else {
			return RedstoneMnemonics.MULPB + '\t' + Helpers.toHex(offset, 2);
		}
	}
}
