package drlc.generate.drc1.instruction;

import drlc.Global;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;

public class InstructionNoOp extends Instruction {
	
	public InstructionNoOp() {
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
		return RedstoneOpcodes.get(RedstoneMnemonics.NOP).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.NOP;
	}
}
