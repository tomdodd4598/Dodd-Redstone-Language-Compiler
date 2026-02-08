package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionSubtract extends InstructionAddress {
	
	public InstructionSubtract(LowDataInfo dataInfo) {
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
		return EdsacOpcodes.SUBTRACT;
	}
}
