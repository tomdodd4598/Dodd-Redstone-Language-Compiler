package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.RedstoneOptimization.ImmediateReplacementInfo;

public interface IInstructionImmediate {
	
	public boolean isUnnecessaryImmediate();
	
	public ImmediateReplacementInfo getImmediateReplacementInfo();
	
	public Short getRegisterValue();
}
