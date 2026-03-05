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
	public boolean isAccumulatorUsed() {
		return false;
	}
	
	@Override
	public boolean isAccumulatorModified() {
		return false;
	}
	
	@Override
	public boolean isDataToMemory() {
		return true;
	}

	@Override
	public boolean isLoadStoreBarrier() {
		return true;
	}

	@Override
	public boolean isUnknownMemoryAccess() {
		return true;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.VERIFY;
	}
}
