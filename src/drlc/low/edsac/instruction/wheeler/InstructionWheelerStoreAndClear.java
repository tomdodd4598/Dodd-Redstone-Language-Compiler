package drlc.low.edsac.instruction.wheeler;

import drlc.intermediate.component.Function;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionWheelerStoreAndClear extends Instruction {
	
	public final Function function;
	public final int section;
	public final int offset;
	public Integer address;
	
	public InstructionWheelerStoreAndClear(Function function, int section, int offset) {
		this.function = function;
		this.section = section;
		this.offset = offset;
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
		return EdsacOpcodes.STORE_AND_CLEAR + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
