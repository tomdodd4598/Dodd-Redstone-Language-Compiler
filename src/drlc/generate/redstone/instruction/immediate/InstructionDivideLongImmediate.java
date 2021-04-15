package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.set.InstructionSetNegative;

public class InstructionDivideLongImmediate extends InstructionALULongImmediate {
	
	public InstructionDivideLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 1;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		if (value == -1) {
			return new InstructionSetNegative();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.DIVLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.DIVLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
