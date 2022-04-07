package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.set.InstructionSetNot;

public class InstructionXorLongImmediate extends InstructionALULongImmediate {
	
	public InstructionXorLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		if (value == -1) {
			return new InstructionSetNot();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.XORLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.XORLI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
