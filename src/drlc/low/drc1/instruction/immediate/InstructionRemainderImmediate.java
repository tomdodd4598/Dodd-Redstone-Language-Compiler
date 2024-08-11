package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.Instruction;

public class InstructionRemainderImmediate extends InstructionALUImmediate {
	
	public InstructionRemainderImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (value == 1 || value == -1) {
			return new InstructionLoadImmediate((short) 0);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.REMI, RedstoneMnemonics.REMLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.REMI, RedstoneMnemonics.REMLI);
	}
}
