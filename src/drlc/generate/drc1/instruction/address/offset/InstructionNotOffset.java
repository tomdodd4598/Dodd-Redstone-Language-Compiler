package drlc.generate.drc1.instruction.address.offset;

import drlc.Helper;
import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.RedstoneRoutine;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionNotOffset extends InstructionALUOffset {
	
	public InstructionNotOffset(DataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionNotOffset(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.NOTNB).concat(Helper.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.NOTPB).concat(Helper.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.NOTNB.concat("\t").concat(Helper.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.NOTPB.concat("\t").concat(Helper.toHex(offset, 2));
		}
	}
}
