package drlc.low.edsac.instruction;

import drlc.low.*;
import drlc.low.edsac.*;

public abstract class Instruction extends LowInstruction {
	
	public Instruction() {
		super();
	}
	
	public int size() {
		return 1;
	}
	
	public abstract boolean isAccumulatorUsed();
	
	public abstract boolean isAccumulatorModified();
	
	public abstract boolean isAccumulatorCleared();
	
	public abstract Instruction getReplacement(EdsacRoutine routine);
	
	public abstract LowDataInfo getReadDataInfo();
	
	public abstract LowDataInfo getWriteDataInfo();
	
	public abstract boolean isUnknownMemoryAccess();
	
	public abstract boolean isLoadStoreBarrier();
	
	/** Ignores code sectioning! */
	public abstract Instruction getCompressedWithNextInstruction(EdsacRoutine routine, Instruction next, boolean sameSection);
	
	public abstract boolean isProtected();
	
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
