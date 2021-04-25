package drlc.generate.drc1.instruction.address;

import drlc.Helper;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionStore extends InstructionAddress implements IInstructionStoreAddress {
	
	public InstructionStore(DataInfo info) {
		super(info);
	}
	
	@Override
	public boolean isRegisterModified() {
		return false;
	}
	
	@Override
	public boolean isRegisterExported() {
		return true;
	}
	
	@Override
	public DataInfo getStoredData() {
		return info;
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneRoutine routine) {
		return new InstructionStore(routine.dataInfo(info.argName));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.STA).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.STA.concat("\t").concat(Helper.toHex(address, 2));
	}
}
