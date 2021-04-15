package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionOrImmediate extends InstructionALUImmediate {
	
	public InstructionOrImmediate(short value) {
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
		return RedstoneOpcodes.get(RedstoneMnemonics.ORI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ORI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
