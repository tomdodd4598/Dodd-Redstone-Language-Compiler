package drlc.generate.redstone.instruction.subroutine;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionLoadBasePointer extends Instruction {
	
	public final short value;
	
	public InstructionLoadBasePointer(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public boolean isRegisterModified() {
		return false;
	}
	
	@Override
	public boolean isRegisterExported() {
		return false;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDBP).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDBP.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
