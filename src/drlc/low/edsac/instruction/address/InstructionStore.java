package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionStore extends InstructionAddress {
	
	public InstructionStore(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isDataFromMemory() {
		return false;
	}
	
	@Override
	public boolean isDataToMemory() {
		return true;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.STORE;
	}
}
