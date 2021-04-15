package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionXorLongImmediate extends InstructionALULongImmediate {
	
	public InstructionXorLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.XORLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.XORLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
