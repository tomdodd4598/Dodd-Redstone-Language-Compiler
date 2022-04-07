package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

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
		return RedstoneOpcodes.get(RedstoneMnemonics.REMLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.REMLI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
