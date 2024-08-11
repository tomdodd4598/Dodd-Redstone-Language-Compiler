package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.set.InstructionSetNegative;

public class InstructionMultiplyImmediate extends InstructionALUImmediate {
	
	public InstructionMultiplyImmediate(short value) {
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
		else if (value == 0) {
			return new InstructionLoadImmediate((short) 0);
		}
		else if (value > 0 && RedstoneCode.isPowerOfTwo(value)) {
			return new InstructionLeftShiftImmediate(RedstoneCode.log2(value));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.MULI, RedstoneMnemonics.MULLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.MULI, RedstoneMnemonics.MULLI);
	}
}
