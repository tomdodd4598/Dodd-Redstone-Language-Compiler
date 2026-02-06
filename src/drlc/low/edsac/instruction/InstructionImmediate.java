package drlc.low.edsac.instruction;

import drlc.low.edsac.*;

public abstract class InstructionImmediate extends Instruction {
	
	public final long value;
	
	protected InstructionImmediate(long value) {
		super();
		this.value = value;
	}
	
	protected abstract String opcode();
	
	@Override
	public String toAssembly() {
		return opcode() + EdsacCode.instructionArgument(value) + EdsacOpcodes.SHORT;
	}
}
