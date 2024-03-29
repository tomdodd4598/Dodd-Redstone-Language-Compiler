package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLoadImmediate extends InstructionImmediate implements IInstructionLoadImmediate {
	
	public InstructionLoadImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public Short getRegisterValue() {
		return value;
	}
	
	@Override
	public short getLoadedValue() {
		return value;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		if (next instanceof InstructionLoadImmediate) {
			return next;
		}
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDAI) + Helpers.toBinary(value, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDAI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
