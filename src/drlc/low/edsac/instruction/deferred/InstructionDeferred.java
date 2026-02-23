package drlc.low.edsac.instruction.deferred;

import drlc.intermediate.component.Function;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public abstract class InstructionDeferred extends Instruction {
	
	public final Function function;
	public final int section;
	public final Instruction target;
	public Integer address;
	
	public InstructionDeferred(Function function, int section, Instruction target) {
		this.function = function;
		this.section = section;
		this.target = target;
	}
	
	protected abstract char opcode();
	
	@Override
	public String toAssembly(Integer offset) {
		return opcode() + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
