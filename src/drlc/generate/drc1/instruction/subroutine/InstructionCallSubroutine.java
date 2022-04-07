package drlc.generate.drc1.instruction.subroutine;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionCallSubroutine extends Instruction {
	
	public final boolean indirectCall;
	public Short returnAddress;
	
	public InstructionCallSubroutine(boolean indirectCall) {
		super();
		this.indirectCall = indirectCall;
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return true;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.CALL).concat(Helpers.toBinary(returnAddress, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.CALL;
	}
}
