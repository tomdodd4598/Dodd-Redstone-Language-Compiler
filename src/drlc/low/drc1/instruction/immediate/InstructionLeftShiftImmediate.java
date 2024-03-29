package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLeftShiftImmediate extends InstructionALUImmediate {
	
	public InstructionLeftShiftImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		short shift = RedstoneCode.shiftBits(value);
		if (value != shift) {
			return new InstructionLeftShiftImmediate(shift);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		/*if (next instanceof InstructionLeftShiftImmediate) {
			InstructionLeftShiftImmediate lsh = (InstructionLeftShiftImmediate) next;
			return new InstructionLeftShiftImmediate((short) (value + lsh.value));
		}*/
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LSHI) + Helpers.toBinary(value, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LSHI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
