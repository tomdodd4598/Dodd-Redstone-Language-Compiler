package drlc.low.edsac.instruction;

import drlc.low.edsac.EdsacOpcodes;
import drlc.low.instruction.LowInstruction;

public abstract class Instruction extends LowInstruction {
	
	public Instruction() {
		super();
	}
	
	public int size() {
		return 1;
	}
	
	protected String suffix(String str, Integer offset) {
		switch (str) {
			case EdsacOpcodes.SHORT:
				return offset == null ? EdsacOpcodes.SHORT : EdsacOpcodes.THETA;
			default:
				throw new IllegalArgumentException(String.format("Offset mapping for suffix \"%s\" not implemented!", str));
		}
	}
	
	public abstract String toAssembly(Integer offset);
	
	@Override
	public final String toString() {
		return toAssembly(null);
	}
}
