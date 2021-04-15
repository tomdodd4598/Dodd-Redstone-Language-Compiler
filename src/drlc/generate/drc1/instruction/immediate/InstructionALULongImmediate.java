package drlc.generate.drc1.instruction.immediate;

import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.InstructionNoOp;

public abstract class InstructionALULongImmediate extends InstructionLongImmediate {
	
	public InstructionALULongImmediate(short value) {
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
