package drlc.low.edsac.instruction.jump;

import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public abstract class InstructionJump extends Instruction {
	
	public final int section;
	public Integer address;
	
	public InstructionJump(int section) {
		super();
		this.section = section;
	}

	@Override
	public boolean isAccumulatorUsed() {
		return true;
	}

	@Override
	public boolean isLoadStoreBarrier() {
		return true;
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
	public Instruction getCompressedWithNextInstruction(EdsacRoutine routine, Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public boolean isProtected() {
		return false;
	}
	
	protected abstract char opcode();
	
	@Override
	public String toAssembly(Integer offset) {
		return opcode() + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
