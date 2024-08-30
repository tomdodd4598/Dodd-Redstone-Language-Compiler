package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.pointer.InstructionDereferenceA;

public class InstructionLoadAddressOffset extends InstructionAddressOffset {
	
	public InstructionLoadAddressOffset(LowDataInfo dataInfo) {
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
			return new InstructionLoadAddressOffset(dataInfo.offsetBy(iai.value));
		}
		else if (next instanceof InstructionSubtractImmediate isi) {
			return new InstructionLoadAddressOffset(dataInfo.offsetBy(-isi.value));
		}
		else if (next instanceof InstructionDereferenceA) {
			return new InstructionLoadAOffset(dataInfo);
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
		return toBinary(longAddress, RedstoneMnemonics.LDIPB, RedstoneMnemonics.LDINB);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.LDIPB, RedstoneMnemonics.LDINB);
	}
}
