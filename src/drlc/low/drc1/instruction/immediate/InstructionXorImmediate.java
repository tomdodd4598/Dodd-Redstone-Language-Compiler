package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.set.InstructionSetNot;

public class InstructionXorImmediate extends InstructionALUImmediate {
	
	public InstructionXorImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (value == -1) {
			return new InstructionSetNot();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.XORI, RedstoneMnemonics.XORLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.XORI, RedstoneMnemonics.XORLI);
	}
}
