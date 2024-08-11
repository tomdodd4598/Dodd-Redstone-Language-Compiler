package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.Instruction;

public class InstructionOrImmediate extends InstructionALUImmediate {
	
	public InstructionOrImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (value == -1) {
			return new InstructionLoadImmediate((short) -1);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.ORI, RedstoneMnemonics.ORLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.ORI, RedstoneMnemonics.ORLI);
	}
}
