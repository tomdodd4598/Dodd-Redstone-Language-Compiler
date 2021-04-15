package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneCode;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionAddLongImmediate extends InstructionALULongImmediate {
	
	public InstructionAddLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		if (!RedstoneCode.isLongImmediate((short) (-value))) {
			return new InstructionSubtractImmediate((short) (-value));
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.ADDLI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ADDLI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
