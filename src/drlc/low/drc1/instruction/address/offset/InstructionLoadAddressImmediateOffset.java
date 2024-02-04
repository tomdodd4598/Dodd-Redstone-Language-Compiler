package drlc.low.drc1.instruction.address.offset;

import drlc.*;
import drlc.low.LowDataInfo;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLoadAddressImmediateOffset extends InstructionAddressOffset {
	
	public InstructionLoadAddressImmediateOffset(LowDataInfo info) {
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
		return new InstructionLoadAddressImmediateOffset(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		if (offset < 0) {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDINB) + Helpers.toBinary(-offset, 8);
		}
		else {
			return RedstoneOpcodes.get(RedstoneMnemonics.LDIPB) + Helpers.toBinary(offset, 8);
		}
	}
	
	@Override
	public String toString() {
		if (offset < 0) {
			return RedstoneMnemonics.LDINB + '\t' + Global.IMMEDIATE + Helpers.toHex(-offset);
		}
		else {
			return RedstoneMnemonics.LDIPB + '\t' + Global.IMMEDIATE + Helpers.toHex(offset);
		}
	}
}
