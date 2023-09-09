package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.set.InstructionSetNegative;

public class InstructionDivideLongImmediate extends InstructionALULongImmediate {
	
	public InstructionDivideLongImmediate(short value) {
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
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.DIVLI) + Global.ZERO_8;
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.DIVLI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
