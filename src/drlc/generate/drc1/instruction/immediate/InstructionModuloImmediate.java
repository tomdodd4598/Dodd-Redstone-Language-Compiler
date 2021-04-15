package drlc.generate.drc1.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionModuloImmediate extends InstructionALUImmediate {
	
	public InstructionModuloImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		if (value == 1) {
			return new InstructionLoadImmediate((short) 0);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.MODI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.MODI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
