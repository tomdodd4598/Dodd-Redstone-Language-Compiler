package drlc.low.edsac.instruction;

import drlc.low.LowDataInfo;
import drlc.low.edsac.EdsacRoutine;

public class InstructionRaw extends Instruction {
	
	protected final String str;
	
	public InstructionRaw(String str) {
		super();
		this.str = str;
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
	public boolean isAccumulatorCleared() {
		return false;
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
		return true;
	}

	@Override
	public boolean isLoadStoreBarrier() {
		return true;
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
		return str;
	}
}
