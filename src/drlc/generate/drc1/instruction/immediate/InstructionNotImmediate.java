package drlc.generate.drc1.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.drc1.RedstoneCode;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionNotImmediate extends InstructionALUImmediate {
	
	public InstructionNotImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		short complement = (short) (~((short) value));
		if (!RedstoneCode.isLongImmediate(complement)) {
			return new InstructionLoadImmediate(complement);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.NOTI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.NOTI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
