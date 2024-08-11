package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.instruction.Instruction;

public interface IInstructionImmediate {
	
	public boolean isUnnecessaryImmediate();
	
	public Instruction getImmediateReplacement();
	
	public Short getRegisterValue();
}
