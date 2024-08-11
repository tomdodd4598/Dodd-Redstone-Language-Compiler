package drlc.low.drc1.instruction.pointer;

import drlc.Global;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionDereferenceA extends Instruction {
	
	public InstructionDereferenceA() {
		super();
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
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
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.DEA) + Global.ZERO_8};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.DEA;
	}
}
