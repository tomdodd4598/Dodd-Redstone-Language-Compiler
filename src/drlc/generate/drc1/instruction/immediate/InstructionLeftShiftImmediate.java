package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLeftShiftImmediate extends InstructionALUImmediate {
	
	public InstructionLeftShiftImmediate(short value) {
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
			return new InstructionLeftShiftImmediate(shift);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		/*if (next instanceof InstructionLeftShiftImmediate) {
			InstructionLeftShiftImmediate lsh = (InstructionLeftShiftImmediate) next;
			return new InstructionLeftShiftImmediate((short) (value + lsh.value));
		}*/
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LSHI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LSHI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
