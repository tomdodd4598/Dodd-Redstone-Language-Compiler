package drlc.low.drc1.instruction.jump;

import drlc.Helpers;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.immediate.IInstructionImmediate;
import drlc.low.drc1.instruction.set.*;

public class InstructionConditionalJumpIfMoreThanOrEqualToZero extends InstructionConditionalJump {
	
	public InstructionConditionalJumpIfMoreThanOrEqualToZero(short section) {
		super(section);
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return true;
	}
	
	@Override
	public Instruction getReplacementConditionalJump(Instruction previous) {
		if (previous instanceof InstructionSetIsLessThanOrEqualToZero) {
			return new InstructionJump(section);
		}
		else if (previous instanceof InstructionSetIsLessThanZero) {
			return new InstructionJump(section);
		}
		else if (previous instanceof InstructionSetIsMoreThanOrEqualToZero) {
			return new InstructionJump(section);
		}
		else if (previous instanceof InstructionSetIsMoreThanZero) {
			return new InstructionJump(section);
		}
		else if (previous instanceof InstructionSetIsNotZero) {
			return new InstructionJump(section);
		}
		else if (previous instanceof InstructionSetIsZero) {
			return new InstructionJump(section);
		}
		else if (previous instanceof InstructionSetNegative) {
			return new InstructionConditionalJumpIfLessThanOrEqualToZero(section);
		}
		else if (previous instanceof InstructionSetNot) {
			return new InstructionConditionalJumpIfLessThanZero(section);
		}
		else if (previous instanceof IInstructionImmediate) {
			IInstructionImmediate immediate = (IInstructionImmediate) previous;
			Short value = immediate.getRegisterValue();
			if (value != null) {
				return value >= 0 ? new InstructionJump(section) : new InstructionNoOp();
			}
		}
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.JMEZ).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JMEZ.concat("\t").concat(Helpers.toHex(address, 2));
	}
}
