package drlc.generate.drc1.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;

public class InstructionLoadLongImmediate extends InstructionLongImmediate implements IInstructionLoadImmediate {
	
	public InstructionLoadLongImmediate(short value) {
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
		return RedstoneOpcodes.get(RedstoneMnemonics.LDALI).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDALI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
