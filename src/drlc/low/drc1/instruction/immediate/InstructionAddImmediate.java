package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionAddImmediate extends InstructionALUImmediate {
	
	public InstructionAddImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (RedstoneCode.isLongImmediate(value) && !RedstoneCode.isLongImmediate((short) -value)) {
			return new InstructionSubtractImmediate((short) -value);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.ADDI, RedstoneMnemonics.ADDLI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.ADDI, RedstoneMnemonics.ADDLI);
	}
}
