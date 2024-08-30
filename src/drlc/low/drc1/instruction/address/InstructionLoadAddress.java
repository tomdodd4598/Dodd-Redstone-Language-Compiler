package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.pointer.InstructionDereferenceA;

public class InstructionLoadAddress extends InstructionAddress {
	
	public InstructionLoadAddress(LowDataInfo dataInfo) {
		super(dataInfo);
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
		if (next instanceof InstructionAddImmediate iai) {
			return new InstructionLoadAddress(dataInfo.offsetBy(iai.value));
		}
		else if (next instanceof InstructionSubtractImmediate isi) {
			return new InstructionLoadAddress(dataInfo.offsetBy(-isi.value));
		}
		else if (next instanceof InstructionDereferenceA) {
			return new InstructionLoadA(dataInfo);
		}
		return null;
	}
	
	@Override
	public boolean isDataFromMemory() {
		return true;
	}
	
	@Override
	public boolean isDataToMemory() {
		return false;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.LDAI, RedstoneMnemonics.LDALI);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LDAI, RedstoneMnemonics.LDALI);
	}
}
