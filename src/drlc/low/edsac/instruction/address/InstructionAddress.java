package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public abstract class InstructionAddress extends Instruction {
	
	public LowDataInfo dataInfo;
	public Integer address;
	
	protected InstructionAddress(LowDataInfo dataInfo) {
		super();
		this.dataInfo = dataInfo;
	}
	
	@Override
	public LowDataInfo getDataInfo() {
		return dataInfo;
	}
	
	@Override
	public void regenerateDataInfo() {
		dataInfo = dataInfo.getRegeneratedDataInfo();
	}
	
	@Override
	public LowDataInfo getReadDataInfo() {
		return isDataFromMemory() ? dataInfo : null;
	}
	
	@Override
	public LowDataInfo getWriteDataInfo() {
		return isDataToMemory() ? dataInfo : null;
	}
	
	@Override
	public boolean isAccumulatorCleared() {
		return false;
	}
	
	@Override
	public Instruction getReplacement(EdsacRoutine routine) {
		return null;
	}
	
	@Override
	public boolean isUnknownMemoryAccess() {
		return false;
	}
	
	@Override
	public boolean isLoadStoreBarrier() {
		return false;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(EdsacRoutine routine, Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public boolean isProtected() {
		return false;
	}
	
	@Override
	public abstract boolean isDataFromMemory();
	
	@Override
	public abstract boolean isDataToMemory();
	
	protected abstract char opcode();
	
	@Override
	public String toAssembly(Integer offset) {
		return opcode() + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
