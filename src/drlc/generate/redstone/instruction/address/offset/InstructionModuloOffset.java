package drlc.generate.redstone.instruction.address.offset;

import drlc.Helper;
import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.RedstoneRoutine;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionModuloOffset extends InstructionALUOffset {
	
	public InstructionModuloOffset(DataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionModuloOffset(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.MODNB).concat(Helper.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.MODPB).concat(Helper.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.MODNB.concat("\t").concat(Helper.toHex(-offset, 2));
		}
		else {
			return RedstoneMnemonics.MODPB.concat("\t").concat(Helper.toHex(offset, 2));
		}
	}
}
