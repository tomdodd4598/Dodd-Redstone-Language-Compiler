package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.set.InstructionSetNegative;

public class InstructionMultiplyLongImmediate extends InstructionALULongImmediate {
	
	public InstructionMultiplyLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 1;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
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
		return RedstoneMnemonics.MULLI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
