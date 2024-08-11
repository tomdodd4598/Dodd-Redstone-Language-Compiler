package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionNotImmediate extends InstructionALUImmediate {
	
	public InstructionNotImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (RedstoneCode.isLongImmediate(value) && !RedstoneCode.isLongImmediate((short) ~value)) {
			return new InstructionLoadImmediate((short) ~value);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Short getRegisterValue() {
		return (short) ~value;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		if (next instanceof InstructionLoadImmediate || next instanceof InstructionNotImmediate) {
			return next;
		}
		return null;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.NOTI, RedstoneMnemonics.NOTLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.NOTI, RedstoneMnemonics.NOTLI);
	}
}
