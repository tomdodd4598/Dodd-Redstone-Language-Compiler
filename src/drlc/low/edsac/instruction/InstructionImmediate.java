package drlc.low.edsac.instruction;

import drlc.low.LowDataInfo;
import drlc.low.edsac.*;

public abstract class InstructionImmediate extends Instruction {
	
	public final long value;
	
	protected InstructionImmediate(long value) {
		super();
		this.value = value;
	}
	
	protected abstract char opcode();
	
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
		return opcode() + EdsacCode.instructionArgument(value) + EdsacOpcodes.SHORT;
	}
}
