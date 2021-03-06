package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

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
		return RedstoneOpcodes.get(RedstoneMnemonics.ORLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ORLI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
