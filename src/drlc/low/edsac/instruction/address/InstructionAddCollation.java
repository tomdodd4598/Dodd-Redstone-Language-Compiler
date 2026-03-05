package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionAddCollation extends InstructionAddress {
	
	public InstructionAddCollation(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isDataFromMemory() {
		return true;
	}

	@Override
	public boolean isAccumulatorUsed() {
		return true;
	}

	@Override
	public boolean isAccumulatorModified() {
		return true;
	}
	
	@Override
	public boolean isDataToMemory() {
		return false;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.ADD_COLLATION;
	}
}
