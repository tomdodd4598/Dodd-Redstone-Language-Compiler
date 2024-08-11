package drlc.low.drc1.instruction.subroutine;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

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
	public int size(boolean longAddress) {
		return longAddress ? 2 : 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		if (longAddress) {
			return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.CALLF) + Global.ZERO_8, Helpers.toBinary(returnAddress, 16)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.CALL) + Helpers.toBinary(returnAddress, 8)};
		}
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return longAddress ? RedstoneMnemonics.CALLF : RedstoneMnemonics.CALL;
	}
}
