package drlc.generate.redstone.instruction.subroutine;

import drlc.Global;
import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionAddToStackPointer extends Instruction {

	public Short value;
	
	public InstructionAddToStackPointer() {
		super();
	}
	
	public InstructionAddToStackPointer(short value) {
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
		return RedstoneOpcodes.get(RedstoneMnemonics.ADDSP).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ADDSP.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
