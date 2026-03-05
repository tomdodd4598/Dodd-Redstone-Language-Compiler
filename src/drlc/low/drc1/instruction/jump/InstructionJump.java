package drlc.low.drc1.instruction.jump;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionJump extends Instruction {
	
	public final int section;
	public Short address;
	public boolean longAddress = false;
	
	public InstructionJump(int section) {
		super();
		this.section = section;
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
	public boolean isUnknownMemoryAccess() {
		return false;
	}
	
	@Override
	public boolean isLoadStoreBarrier() {
		return true;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	public boolean isDefiniteJump() {
		return true;
	}
	
	@Override
	public int size(boolean longAddress) {
		return longAddress && this.longAddress ? 2 : 1;
	}
	
	protected String[] toBinary(boolean longAddress, String mnemonic, String longMnemonic) {
		if (longAddress && this.longAddress) {
			return new String[] {RedstoneOpcodes.get(longMnemonic) + Global.ZERO_8, Helpers.toBinary(address, 16)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(mnemonic) + Helpers.toBinary(address, 8)};
		}
	}
	
	protected String toAssembly(boolean longAddress, String mnemonic, String longMnemonic) {
		String hex = Helpers.toHex(address, longAddress ? 4 : 2);
		if (longAddress && this.longAddress) {
			return longMnemonic + '\t' + hex;
		}
		else {
			return mnemonic + '\t' + hex;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.JMP, RedstoneMnemonics.JMPL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.JMP, RedstoneMnemonics.JMPL);
	}
}
