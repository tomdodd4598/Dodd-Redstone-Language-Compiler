package drlc.generate.drc1.instruction.subroutine;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLoadStackPointer extends Instruction {
	
	public final short value;
	
	public InstructionLoadStackPointer(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		if (sameSection && next instanceof InstructionLoadStackPointer) {
			return next;
		}
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDSP).concat(Helpers.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDSP.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
