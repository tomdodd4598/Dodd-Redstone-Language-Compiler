package drlc.generate.redstone.instruction.immediate;

import drlc.generate.redstone.instruction.IInstructionLoad;

public interface IInstructionLoadImmediate extends IInstructionLoad {
	
	public short getLoadedValue();
}
