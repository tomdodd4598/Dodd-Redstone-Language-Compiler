package drlc.low.edsac.instruction.deferred;

import drlc.intermediate.component.Function;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionDeferredStoreAndClear extends Instruction {
	
	public final Function function;
	public final int section;
	public final Instruction target;
	public Integer address;
	
	public InstructionDeferredStoreAndClear(Function function, int section, Instruction target) {
		this.function = function;
		this.section = section;
		this.target = target;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return EdsacOpcodes.STORE_AND_CLEAR + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
