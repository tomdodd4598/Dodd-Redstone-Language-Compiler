package drlc.low.drc1.instruction.subroutine;

import drlc.*;
import drlc.intermediate.component.Function;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLoadSubroutineAddress extends Instruction {
	
	public final Function function;
	public Short value;
	
	public InstructionLoadSubroutineAddress(Function function) {
		super();
		this.function = function;
	}
	
	public void setValue(short value) {
		if (this.value == null) {
			this.value = value;
		}
		else {
			throw new UnsupportedOperationException(String.format("Attempted to modify non-null immediate call address!"));
		}
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
		if (sameSection && next instanceof InstructionLoadSubroutineAddress) {
			return next;
		}
		return null;
	}
	
	@Override
	public int size(boolean longAddress) {
		return longAddress ? 2 : 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		if (longAddress) {
			return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.LDALI) + Global.ZERO_8, Helpers.toBinary(value, 16)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.LDAI) + Helpers.toBinary(value, 8)};
		}
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		if (longAddress) {
			return RedstoneMnemonics.LDALI + '\t' + Helpers.toHex(value, 4) + '\t' + function.asmString();
		}
		else {
			return RedstoneMnemonics.LDAI + '\t' + Helpers.toHex(value, 2) + '\t' + function.asmString();
		}
	}
}
