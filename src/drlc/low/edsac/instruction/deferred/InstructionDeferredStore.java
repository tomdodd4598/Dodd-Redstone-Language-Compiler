package drlc.low.edsac.instruction.deferred;

import drlc.intermediate.component.Function;
import drlc.low.edsac.EdsacOpcodes;
import drlc.low.edsac.instruction.Instruction;

public class InstructionDeferredStore extends InstructionDeferred {
	
	public InstructionDeferredStore(Function function, int section, Instruction target) {
		super(function, section, target);
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.STORE;
	}
}
