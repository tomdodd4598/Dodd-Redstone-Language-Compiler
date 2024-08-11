package drlc.low.drc1.instruction.immediate;

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
		if (RedstoneCode.isLongImmediate(value) && !RedstoneCode.isLongImmediate((short) ~value)) {
			return new InstructionNotImmediate((short) ~value);
		}
		else {
			return null;
		}
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
		if (next instanceof InstructionLoadImmediate || next instanceof InstructionNotImmediate) {
			return next;
		}
		return null;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.LDAI, RedstoneMnemonics.LDALI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LDAI, RedstoneMnemonics.LDALI);
	}
}
