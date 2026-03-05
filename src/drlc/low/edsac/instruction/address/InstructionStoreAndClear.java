package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionStoreAndClear extends InstructionAddress {
	
	public InstructionStoreAndClear(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isDataFromMemory() {
		return false;
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
	public boolean isAccumulatorCleared() {
		return true;
	}
	
	@Override
	public boolean isDataToMemory() {
		return true;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.STORE_AND_CLEAR;
	}
}
