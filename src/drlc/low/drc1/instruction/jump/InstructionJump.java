package drlc.low.drc1.instruction.jump;

import drlc.Helpers;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionJump extends Instruction {
	
	public final int section;
	public Short address;
	
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
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	public boolean isDefiniteJump() {
		return true;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.JMP) + Helpers.toBinary(address, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JMP + '\t' + Helpers.toHex(address, 2);
	}
}
