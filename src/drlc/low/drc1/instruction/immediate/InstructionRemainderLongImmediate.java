package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionRemainderLongImmediate extends InstructionALULongImmediate {
	
	public InstructionRemainderLongImmediate(short value) {
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
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.REMLI) + Global.ZERO_8;
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.REMLI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
