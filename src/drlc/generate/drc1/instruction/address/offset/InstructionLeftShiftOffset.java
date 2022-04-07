package drlc.generate.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLeftShiftOffset extends InstructionALUOffset {
	
	public InstructionLeftShiftOffset(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionLeftShiftOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.LSHNB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.LSHPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.LSHNB.concat("\t").concat(Helpers.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.LSHPB.concat("\t").concat(Helpers.toHex(offset, 2));
		}
	}
}
