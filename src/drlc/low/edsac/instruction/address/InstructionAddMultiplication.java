package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionAddMultiplication extends InstructionAddress {
	
	public InstructionAddMultiplication(LowDataInfo dataInfo) {
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
	protected String opcode() {
		return EdsacOpcodes.ADD_MULTIPLICATION;
	}
}
