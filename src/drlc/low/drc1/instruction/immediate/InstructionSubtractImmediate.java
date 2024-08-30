package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionSubtractImmediate extends InstructionALUImmediate {
	
	public InstructionSubtractImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (RedstoneCode.isLong(value) && !RedstoneCode.isLong((short) -value)) {
			return new InstructionAddImmediate((short) -value);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.SUBI, RedstoneMnemonics.SUBLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.SUBI, RedstoneMnemonics.SUBLI);
	}
}
