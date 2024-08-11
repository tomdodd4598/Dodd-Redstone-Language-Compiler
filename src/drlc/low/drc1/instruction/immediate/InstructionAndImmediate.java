package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.Instruction;

public class InstructionAndImmediate extends InstructionALUImmediate {
	
	public InstructionAndImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == -1;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (value == 0) {
			return new InstructionLoadImmediate((short) 0);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.ANDI, RedstoneMnemonics.ANDLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.ANDI, RedstoneMnemonics.ANDLI);
	}
}
