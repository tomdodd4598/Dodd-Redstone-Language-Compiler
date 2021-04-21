package drlc.generate.drc1.instruction.jump;

import drlc.Helper;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionJump extends Instruction {
	
	public final short section;
	public Short address;
	
	public InstructionJump(short section) {
		super();
		this.section = section;
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
		return RedstoneOpcodes.get(RedstoneMnemonics.JMP).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JMP.concat("\t").concat(Helper.toHex(address, 2));
	}
}
