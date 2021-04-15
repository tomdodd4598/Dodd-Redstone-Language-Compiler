package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneCode;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.set.InstructionSetNegative;

public class InstructionMultiplyLongImmediate extends InstructionALULongImmediate {
	
	public InstructionMultiplyLongImmediate(short value) {
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
		else if (value > 0 && RedstoneCode.isPowerOfTwo(value)) {
			return new InstructionLeftShiftImmediate(RedstoneCode.log2(value));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.MULLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.MULLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
