package drlc.low.drc1.instruction.subroutine;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

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
	public int size(boolean longAddress) {
		return 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.LDSP) + Helpers.toBinary(value, 8)};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.LDSP + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
