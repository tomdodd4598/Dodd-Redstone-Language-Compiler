package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionAddBImmediate extends InstructionALUImmediate {
	
	public InstructionAddBImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.ADDBI) + Helpers.toBinary(value, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ADDBI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
