package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.set.InstructionSetNegative;

public class InstructionDivideImmediate extends InstructionALUImmediate {
	
	public InstructionDivideImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 1;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (value == -1) {
			return new InstructionSetNegative();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.DIVI, RedstoneMnemonics.DIVLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.DIVI, RedstoneMnemonics.DIVLI);
	}
}
