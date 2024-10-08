package drlc.low.drc1.instruction.jump;

import drlc.low.drc1.RedstoneMnemonics;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.immediate.InstructionImmediate;
import drlc.low.drc1.instruction.set.*;

public class InstructionConditionalJumpIfLessThanOrEqualToZero extends InstructionConditionalJump {
	
	public InstructionConditionalJumpIfLessThanOrEqualToZero(int section) {
		super(section);
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return true;
	}
	
	@Override
	public Instruction getReplacementConditionalJump(Instruction previous) {
		if (previous instanceof InstructionSetIsLessThanOrEqualToZero) {
			return new InstructionConditionalJumpIfMoreThanZero(section);
		}
		else if (previous instanceof InstructionSetIsLessThanZero) {
			return new InstructionConditionalJumpIfMoreThanOrEqualToZero(section);
		}
		else if (previous instanceof InstructionSetIsMoreThanOrEqualToZero) {
			return new InstructionConditionalJumpIfLessThanZero(section);
		}
		else if (previous instanceof InstructionSetIsMoreThanZero) {
			return new InstructionConditionalJumpIfLessThanOrEqualToZero(section);
		}
		else if (previous instanceof InstructionSetIsNotZero) {
			return new InstructionConditionalJumpIfZero(section);
		}
		else if (previous instanceof InstructionSetIsZero) {
			return new InstructionConditionalJumpIfNotZero(section);
		}
		else if (previous instanceof InstructionSetNegative) {
			return new InstructionConditionalJumpIfMoreThanOrEqualToZero(section);
		}
		else if (previous instanceof InstructionSetNot) {
			return null;
		}
		else if (previous instanceof InstructionImmediate immediate) {
			Short value = immediate.getRegisterValue();
			if (value != null) {
				return value <= 0 ? new InstructionJump(section) : new InstructionNoOp();
			}
		}
		return null;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return toBinary(longAddress, RedstoneMnemonics.JLEZ, RedstoneMnemonics.JLEZL);
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return toAssembly(longAddress, RedstoneMnemonics.JLEZ, RedstoneMnemonics.JLEZL);
	}
}
