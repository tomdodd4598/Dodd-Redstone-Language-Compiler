package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
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
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		/*if (next instanceof InstructionRightShiftImmediate) {
			InstructionRightShiftImmediate rsh = (InstructionRightShiftImmediate) next;
			return new InstructionRightShiftImmediate((short) (value + rsh.value));
		}*/
		return null;
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
