package drlc.generate.drc1.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionAndLongImmediate extends InstructionALULongImmediate {
	
	public InstructionAndLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == -1;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.ANDLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ANDLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
