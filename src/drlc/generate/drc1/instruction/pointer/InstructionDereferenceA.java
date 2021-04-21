package drlc.generate.drc1.instruction.pointer;

import drlc.Global;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionDereferenceA extends Instruction {
	
	public InstructionDereferenceA() {
		super();
	}
	
	@Override
	public boolean isRegisterModified() {
		return true;
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
		return RedstoneOpcodes.get(RedstoneMnemonics.DEA).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.DEA;
	}
}
