package drlc.low.drc1.instruction.subroutine;

import drlc.Helpers;
import drlc.intermediate.component.Function;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLoadCallAddressImmediate extends Instruction {
	
	public final Function function;
	public Short value;
	
	public InstructionLoadCallAddressImmediate(Function function) {
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
		if (next instanceof InstructionLoadCallAddressImmediate) {
			return next;
		}
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDAI) + Helpers.toBinary(value, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDAI + '\t' + function.asmString();
	}
}
