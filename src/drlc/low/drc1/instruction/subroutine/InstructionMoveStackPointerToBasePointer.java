package drlc.low.drc1.instruction.subroutine;

import drlc.Global;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionMoveStackPointerToBasePointer extends Instruction {
	
	public InstructionMoveStackPointerToBasePointer() {
		super();
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
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.MSPBP) + Global.ZERO_8;
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.MSPBP;
	}
}
