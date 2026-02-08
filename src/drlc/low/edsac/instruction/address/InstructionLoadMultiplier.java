package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionLoadMultiplier extends InstructionAddress {
	
	public InstructionLoadMultiplier(LowDataInfo dataInfo) {
		super(dataInfo);
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
	protected char opcode() {
		return EdsacOpcodes.LOAD_MULTIPLIER;
	}
}
