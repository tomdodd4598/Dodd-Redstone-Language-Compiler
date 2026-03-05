package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionClearAccumulator extends Instruction {
	
	public Integer address;
	
	public InstructionClearAccumulator() {
		super();
	}
	
	@Override
	public boolean isAccumulatorUsed() {
		return false;
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
	public Instruction getReplacement(EdsacRoutine routine) {
		return null;
	}
	
	@Override
	public LowDataInfo getReadDataInfo() {
		return null;
	}
	
	@Override
	public LowDataInfo getWriteDataInfo() {
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
	public String toAssembly(Integer offset) {
		return EdsacOpcodes.STORE_AND_CLEAR + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
