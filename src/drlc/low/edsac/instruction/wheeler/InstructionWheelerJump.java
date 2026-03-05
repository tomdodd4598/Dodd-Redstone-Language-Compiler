package drlc.low.edsac.instruction.wheeler;

import drlc.intermediate.component.Function;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionWheelerJump extends Instruction {
	
	public final Function function;
	public final InstructionWheelerReturn iwr;
	public Integer address;
	
	public InstructionWheelerJump(Function function, InstructionWheelerReturn iwr) {
		this.function = function;
		this.iwr = iwr;
	}
	
	@Override
	public boolean isAccumulatorUsed() {
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
	public boolean isLoadStoreBarrier() {
		return true;
	}
	
	@Override
	public boolean isUnknownMemoryAccess() {
		return true;
	}
	
	@Override
	public boolean isProtected() {
		return true;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(EdsacRoutine routine, Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
