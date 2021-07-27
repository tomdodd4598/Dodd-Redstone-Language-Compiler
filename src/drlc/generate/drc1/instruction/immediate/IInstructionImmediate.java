package drlc.generate.drc1.instruction.immediate;

import drlc.generate.drc1.RedstoneOptimization.ImmediateReplacementInfo;

public interface IInstructionImmediate {
	
	public boolean isUnnecessaryImmediate();
	
	public ImmediateReplacementInfo getImmediateReplacementInfo();
	
	public Short getRegisterValue();
}
