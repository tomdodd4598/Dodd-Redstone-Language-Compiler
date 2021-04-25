package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionAddLongImmediate extends InstructionALULongImmediate {
	
	public InstructionAddLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		if (!RedstoneCode.isLongImmediate((short) (-value))) {
			return new InstructionSubtractImmediate((short) (-value));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.ADDLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ADDLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
