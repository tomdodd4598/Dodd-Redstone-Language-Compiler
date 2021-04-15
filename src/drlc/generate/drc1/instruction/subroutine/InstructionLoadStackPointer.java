package drlc.generate.drc1.instruction.subroutine;

import drlc.Global;
import drlc.Helper;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLoadStackPointer extends Instruction {
	
	public final short value;
	
	public InstructionLoadStackPointer(short value) {
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
		return RedstoneOpcodes.get(RedstoneMnemonics.LDSP).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDSP.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
