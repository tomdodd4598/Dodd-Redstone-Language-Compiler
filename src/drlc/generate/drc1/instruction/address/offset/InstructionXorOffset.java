package drlc.generate.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionXorOffset extends InstructionALUOffset {
	
	public InstructionXorOffset(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionXorOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.XORNB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.XORPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.XORNB.concat("\t").concat(Helpers.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.XORPB.concat("\t").concat(Helpers.toHex(offset, 2));
		}
	}
}
