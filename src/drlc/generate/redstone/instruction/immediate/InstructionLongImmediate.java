package drlc.generate.redstone.instruction.immediate;

import drlc.generate.redstone.instruction.Instruction;

public abstract class InstructionLongImmediate extends Instruction implements IInstructionLongImmediate {
	
	public final short value;
	
	public InstructionLongImmediate(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public boolean precedesData() {
		return true;
	}
}
