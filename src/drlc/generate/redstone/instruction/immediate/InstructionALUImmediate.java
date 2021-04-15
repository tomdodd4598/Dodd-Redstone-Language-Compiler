package drlc.generate.redstone.instruction.immediate;

import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.InstructionNoOp;

public abstract class InstructionALUImmediate extends InstructionImmediate {
	
	public InstructionALUImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isRegisterModified() {
		return true;
	}
	
	@Override
	public boolean isRegisterExported() {
		return false;
	}
	
	public final Instruction getALUImmediateReplacement() {
		if (isUnnecessaryImmediate()) {
			return new InstructionNoOp();
		}
		else {
			return getALUImmediateReplacementInternal();
		}
	}
	
	protected abstract Instruction getALUImmediateReplacementInternal();
}
