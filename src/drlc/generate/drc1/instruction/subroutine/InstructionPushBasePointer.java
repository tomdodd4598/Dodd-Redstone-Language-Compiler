package drlc.generate.drc1.instruction.subroutine;

import drlc.Global;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionPushBasePointer extends Instruction {
	
	public InstructionPushBasePointer() {
		super();
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
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.PSHBP).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.PSHBP;
	}
}
