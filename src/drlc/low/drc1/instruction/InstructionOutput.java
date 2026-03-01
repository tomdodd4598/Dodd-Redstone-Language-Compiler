package drlc.low.drc1.instruction;

import drlc.Global;
import drlc.low.drc1.*;

public class InstructionOutput extends Instruction {
	
	public InstructionOutput() {
		super();
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return true;
	}
	
	@Override
	public boolean isUnknownMemoryAccess() {
		return false;
	}
	
	@Override
	public boolean isLoadStoreBarrier() {
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
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.OUT) + Global.ZERO_8};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.OUT;
	}
}
