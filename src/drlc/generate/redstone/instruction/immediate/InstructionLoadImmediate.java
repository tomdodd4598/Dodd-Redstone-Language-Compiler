package drlc.generate.redstone.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;

public class InstructionLoadImmediate extends InstructionImmediate implements IInstructionLoadImmediate {
	
	public InstructionLoadImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isRegisterModified() {
		return true;
	}
	
	@Override
	public boolean isRegisterExported() {
		return false;
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public short getLoadedValue() {
		return value;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDAI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDAI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
