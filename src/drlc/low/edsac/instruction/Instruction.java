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
		if (str.equals(EdsacOpcodes.SHORT)) {
			return offset == null ? str : EdsacOpcodes.THETA;
		}
		else if (str.equals(EdsacOpcodes.LONG)) {
			return offset == null ? str : (EdsacOpcodes.PI + EdsacOpcodes.THETA);
		}
		else {
			throw new IllegalArgumentException(String.format("Encountered unexpected instruction suffix \"%s\"!", str));
		}
	}
	
	public abstract String toAssembly(Integer offset);
	
	@Override
	public final String toString() {
		return toAssembly(null);
	}
}
