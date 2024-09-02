package drlc.low.edsac.instruction;

import drlc.low.instruction.LowInstruction;

public abstract class Instruction extends LowInstruction {
	
	public Instruction() {
		super();
	}
	
	public abstract int size();
	
	public abstract String toAssembly();
	
	@Override
	public final String toString() {
		return toAssembly();
	}
}
