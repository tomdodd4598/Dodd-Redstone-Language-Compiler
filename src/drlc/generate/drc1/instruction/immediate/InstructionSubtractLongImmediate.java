package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionSubtractLongImmediate extends InstructionALULongImmediate {
	
	public InstructionSubtractLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		if (!RedstoneCode.isLongImmediate((short) (-value))) {
			return new InstructionAddImmediate((short) (-value));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.SUBLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.SUBLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
