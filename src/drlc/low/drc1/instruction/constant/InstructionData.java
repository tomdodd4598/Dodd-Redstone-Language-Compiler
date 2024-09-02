package drlc.low.drc1.instruction.constant;

import java.util.List;
import java.util.stream.Collectors;

import drlc.*;
import drlc.low.drc1.instruction.Instruction;

public abstract class InstructionData extends Instruction {
	
	public InstructionData() {
		super();
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
	
	protected abstract List<Short> values();
	
	@Override
	public int size(boolean longAddress) {
		return values().size();
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return values().stream().map(x -> Helpers.toBinary(x, 16)).toArray(String[]::new);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return values().stream().map(x -> Global.IMMEDIATE + Helpers.toHex(x)).collect(Collectors.joining("\t"));
	}
}
