package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionSubtractBImmediate extends InstructionALUImmediate {
	
	public InstructionSubtractBImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public int size(boolean longAddress) {
		return 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.SUBBI) + Helpers.toBinary(value, 8)};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.SUBBI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
