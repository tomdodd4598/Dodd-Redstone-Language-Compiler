package drlc.generate.drc1.instruction.immediate;

import drlc.generate.drc1.instruction.IInstructionLoad;

public interface IInstructionLoadImmediate extends IInstructionLoad {
	
	public short getLoadedValue();
}
