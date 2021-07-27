package drlc.generate.drc1.instruction.jump;

import drlc.Helper;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.*;
import drlc.generate.drc1.instruction.immediate.IInstructionImmediate;
import drlc.generate.drc1.instruction.set.*;

public class InstructionConditionalJumpIfMoreThanZero extends InstructionConditionalJump {
	
	public InstructionConditionalJumpIfMoreThanZero(short section) {
		super(section);
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return true;
	}
	
	@Override
	public Instruction getReplacementConditionalJump(Instruction previous) {
		if (previous instanceof InstructionSetIsLessThanOrEqualToZero) {
			return new InstructionConditionalJumpIfLessThanOrEqualToZero(section);
		}
		else if (previous instanceof InstructionSetIsLessThanZero) {
			return new InstructionConditionalJumpIfLessThanZero(section);
		}
		else if (previous instanceof InstructionSetIsMoreThanOrEqualToZero) {
			return new InstructionConditionalJumpIfMoreThanOrEqualToZero(section);
		}
		else if (previous instanceof InstructionSetIsMoreThanZero) {
			return new InstructionConditionalJumpIfMoreThanZero(section);
		}
		else if (previous instanceof InstructionSetIsNotZero) {
			return new InstructionConditionalJumpIfNotZero(section);
		}
		else if (previous instanceof InstructionSetIsZero) {
			return new InstructionConditionalJumpIfZero(section);
		}
		else if (previous instanceof InstructionSetNegative) {
			return new InstructionConditionalJumpIfLessThanZero(section);
		}
		else if (previous instanceof InstructionSetNot) {
			return null;
		}
		else if (previous instanceof IInstructionImmediate) {
			IInstructionImmediate immediate = (IInstructionImmediate) previous;
			Short value = immediate.getRegisterValue();
			if (value != null) {
				return value > 0 ? new InstructionJump(section) : new InstructionNoOp();
			}
		}
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.JMZ).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JMZ.concat("\t").concat(Helper.toHex(address, 2));
	}
}
