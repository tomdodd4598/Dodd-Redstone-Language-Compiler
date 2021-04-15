package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionModuloLongImmediate extends InstructionALULongImmediate {
	
	public InstructionModuloLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		if (value == 1 || value == -1) {
			return new InstructionLoadImmediate((short) 0);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.MODLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.MODLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
