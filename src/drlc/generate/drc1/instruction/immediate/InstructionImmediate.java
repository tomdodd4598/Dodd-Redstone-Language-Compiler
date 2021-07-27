package drlc.generate.drc1.instruction.immediate;

import drlc.generate.drc1.RedstoneOptimization.ImmediateReplacementInfo;
import drlc.generate.drc1.instruction.*;

public abstract class InstructionImmediate extends Instruction implements IInstructionImmediate {
	
	public final short value;
	
	public InstructionImmediate(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public ImmediateReplacementInfo getImmediateReplacementInfo() {
		if (isUnnecessaryImmediate()) {
			return new ImmediateReplacementInfo(new InstructionNoOp(), precedesData() ? new InstructionNoOp() : null);
		}
		else {
			Instruction replacement = getImmediateReplacementInternal();
			if (replacement == null || (!precedesData() && replacement.precedesData())) {
				return null;
			}
			else {
				Instruction succeeding = !precedesData() && !replacement.precedesData() ? null : (precedesData() && !replacement.precedesData() ? new InstructionNoOp() : replacement.succeedingData());
				return new ImmediateReplacementInfo(replacement, succeeding);
			}
		}
	}
	
	protected abstract Instruction getImmediateReplacementInternal();
}
