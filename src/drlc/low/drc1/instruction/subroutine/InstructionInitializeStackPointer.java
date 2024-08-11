package drlc.low.drc1.instruction.subroutine;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionInitializeStackPointer extends Instruction {
	
	public InstructionInitializeStackPointer() {
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
	public int size(boolean longAddress) {
		return 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.SUBSP) + Helpers.toBinary(1, 8)};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.SUBSP + '\t' + Global.IMMEDIATE + Helpers.toHex(1);
	}
}
