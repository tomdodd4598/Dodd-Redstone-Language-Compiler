package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionDivideImmediate extends InstructionALUImmediate {
	
	public InstructionDivideImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 1;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.DIVI).concat(Helpers.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.DIVI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
