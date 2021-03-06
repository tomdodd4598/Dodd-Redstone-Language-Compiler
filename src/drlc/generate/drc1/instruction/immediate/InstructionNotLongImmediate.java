package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionNotLongImmediate extends InstructionALULongImmediate {
	
	public InstructionNotLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		short complement = (short) ~value;
		if (!RedstoneCode.isLongImmediate(complement)) {
			return new InstructionLoadImmediate(complement);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Short getRegisterValue() {
		return (short) ~value;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.NOTLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.NOTLI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
