package drlc.generate.drc1.instruction.address.offset;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionLoadImmediateAddressOffset extends InstructionAddressOffset {
	
	public InstructionLoadImmediateAddressOffset(RedstoneDataInfo info) {
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
		return new InstructionLoadImmediateAddressOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDINB).concat(Helpers.toBinary(-offset, 8));
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDIPB).concat(Helpers.toBinary(offset, 8));
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.LDINB.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(-offset));
		}
		else {
			return RedstoneMnemonics.LDIPB.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(offset));
		}
	}
}
