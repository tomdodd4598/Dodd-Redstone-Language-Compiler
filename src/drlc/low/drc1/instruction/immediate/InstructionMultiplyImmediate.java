package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionMultiplyImmediate extends InstructionALUImmediate {
	
	public InstructionMultiplyImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 1;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
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
		return RedstoneOpcodes.get(RedstoneMnemonics.MULI).concat(Helpers.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.MULI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
