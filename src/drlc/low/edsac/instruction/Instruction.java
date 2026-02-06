package drlc.low.edsac.instruction;

import drlc.low.instruction.LowInstruction;

public abstract class Instruction extends LowInstruction {
	
	public Instruction() {
		super();
	}
	
	public int size() {
		return 1;
	}
	
	public abstract String toAssembly();
	
	@Override
	public final String toString() {
		return toAssembly();
	}
}
