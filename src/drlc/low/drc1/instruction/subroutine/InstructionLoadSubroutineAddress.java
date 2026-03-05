package drlc.low.drc1.instruction.subroutine;

import drlc.*;
import drlc.intermediate.component.Function;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLoadSubroutineAddress extends Instruction {
	
	public final Function function;
	public Short value;
	public boolean longAddress = false;
	
	public InstructionLoadSubroutineAddress(Function function) {
		super();
		this.function = function;
	}
	
	public void setValue(short value) {
		this.value = value;
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
	public boolean isUnknownMemoryAccess() {
		return false;
	}
	
	@Override
	public boolean isLoadStoreBarrier() {
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
		return longAddress && this.longAddress ? 2 : 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		if (longAddress && this.longAddress) {
			return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.LDALI) + Global.ZERO_8, Helpers.toBinary(value, 16)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.LDAI) + Helpers.toBinary(value, 8)};
		}
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		String hex = Helpers.toHex(value, longAddress ? 4 : 2);
		if (longAddress && this.longAddress) {
			return RedstoneMnemonics.LDALI + '\t' + hex + '\t' + function.asmString();
		}
		else {
			return RedstoneMnemonics.LDAI + '\t' + hex + '\t' + function.asmString();
		}
	}
}
