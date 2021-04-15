package drlc.generate.redstone.instruction.immediate;

import drlc.generate.redstone.instruction.Instruction;

public abstract class InstructionImmediate extends Instruction implements IInstructionImmediate {
	
	public final short value;
	
	public InstructionImmediate(short value) {
		super();
		this.value = value;
	}
}
