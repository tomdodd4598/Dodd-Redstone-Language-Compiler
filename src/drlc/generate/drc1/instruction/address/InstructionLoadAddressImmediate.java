package drlc.generate.drc1.instruction.address;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLoadAddressImmediate extends InstructionAddress {
	
	public InstructionLoadAddressImmediate(RedstoneDataInfo info) {
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
	public boolean isDataFromMemory() {
		return true;
	}
	
	@Override
	public boolean isDataToMemory() {
		return false;
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionLoadAddressImmediate(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDAI).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDAI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(address));
	}
}
