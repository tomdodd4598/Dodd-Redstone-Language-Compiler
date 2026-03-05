package drlc.low.edsac.instruction.deferred;

import drlc.intermediate.component.Function;
import drlc.low.edsac.EdsacOpcodes;
import drlc.low.edsac.instruction.Instruction;

public class InstructionDeferredStoreAndClear extends InstructionDeferred {
	
	public InstructionDeferredStoreAndClear(Function function, int section, Instruction target) {
		super(function, section, target);
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
	protected char opcode() {
		return EdsacOpcodes.STORE_AND_CLEAR;
	}
}
