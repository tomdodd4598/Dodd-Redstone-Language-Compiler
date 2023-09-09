package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionOrLongImmediate extends InstructionALULongImmediate {
	
	public InstructionOrLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (value == -1) {
			return new InstructionLoadLongImmediate((short) -1);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.ORLI) + Global.ZERO_8;
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ORLI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
