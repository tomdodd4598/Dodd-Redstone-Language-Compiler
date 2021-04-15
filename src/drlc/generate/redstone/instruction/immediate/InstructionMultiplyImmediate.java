package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneCode;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionMultiplyImmediate extends InstructionALUImmediate {
	
	public InstructionMultiplyImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 1;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		if (value == 0) {
			return new InstructionLoadImmediate((short) 0);
		}
		else if (value > 0 && RedstoneCode.isPowerOfTwo(value)) {
			return new InstructionLeftShiftImmediate(RedstoneCode.log2(value));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.MULI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.MULI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
