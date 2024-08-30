package drlc.low.edsac.instruction;

import drlc.low.LowInstruction;

public abstract class Instruction implements LowInstruction {
	
	public Instruction() {}
	
	public abstract int size();
	
	public abstract String toAssembly();
	
	@Override
	public final String toString() {
		return toAssembly();
	}
}
