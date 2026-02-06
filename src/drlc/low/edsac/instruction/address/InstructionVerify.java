package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionVerify extends InstructionAddress {
	
	public InstructionVerify(LowDataInfo dataInfo) {
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
	protected String opcode() {
		return EdsacOpcodes.VERIFY;
	}
}
