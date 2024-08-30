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
		if (sameSection && (next instanceof InstructionMoveStackPointerToBasePointer || next instanceof InstructionLoadBasePointer)) {
			return next;
		}
		return null;
	}
	
	@Override
	public int size(boolean longAddress) {
		return 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.MSPBP) + Global.ZERO_8};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.MSPBP;
	}
}
