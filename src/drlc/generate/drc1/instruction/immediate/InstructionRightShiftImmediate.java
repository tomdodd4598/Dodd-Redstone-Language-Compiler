package drlc.generate.drc1.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.drc1.RedstoneCode;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionRightShiftImmediate extends InstructionALUImmediate {
	
	public InstructionRightShiftImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		short shift = RedstoneCode.shiftBits(value);
		if (value != shift) {
			return new InstructionRightShiftImmediate(shift);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.RSHI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.RSHI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
