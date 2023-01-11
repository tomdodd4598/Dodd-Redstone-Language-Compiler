package drlc.low.drc1.instruction.immediate;

import drlc.low.drc1.instruction.IInstructionLoad;

public interface IInstructionLoadImmediate extends IInstructionLoad, IInstructionImmediate {
	
	public short getLoadedValue();
}
