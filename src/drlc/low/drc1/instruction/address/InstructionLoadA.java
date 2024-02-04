package drlc.low.drc1.instruction.address;

import drlc.Helpers;
import drlc.low.LowDataInfo;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLoadA extends InstructionAddress implements IInstructionLoadAddress {
	
	public InstructionLoadA(LowDataInfo info) {
		super(info);
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
	public LowDataInfo getLoadedData() {
		return info;
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionLoadA(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDA) + Helpers.toBinary(address, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDA + '\t' + Helpers.toHex(address, 2);
	}
}
