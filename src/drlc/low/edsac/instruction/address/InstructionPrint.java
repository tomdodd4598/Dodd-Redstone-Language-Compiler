package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacOpcodes;

public class InstructionPrint extends InstructionAddress {
	
	public InstructionPrint(LowDataInfo dataInfo) {
		super(dataInfo);
	}
	
	@Override
	public boolean isDataFromMemory() {
		return true;
	}
	
	@Override
	public boolean isAccumulatorUsed() {
		return false;
	}
	
	@Override
	public boolean isAccumulatorModified() {
		return false;
	}
	
	@Override
	public boolean isDataToMemory() {
		return false;
	}

	@Override
	public boolean isLoadStoreBarrier() {
		return true;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.PRINT;
	}
}
