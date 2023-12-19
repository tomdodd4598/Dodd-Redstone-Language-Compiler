package drlc.low.drc1.instruction.jump;

import drlc.Helpers;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.immediate.IInstructionImmediate;
import drlc.low.drc1.instruction.set.*;

public class InstructionConditionalJumpIfLessThanZero extends InstructionConditionalJump {
	
	public InstructionConditionalJumpIfLessThanZero(int section) {
		super(section);
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return true;
	}
	
	@Override
	public Instruction getReplacementConditionalJump(Instruction previous) {
		if (previous instanceof InstructionSetIsLessThanOrEqualToZero) {
			return new InstructionNoOp();
		}
		else if (previous instanceof InstructionSetIsLessThanZero) {
			return new InstructionNoOp();
		}
		else if (previous instanceof InstructionSetIsMoreThanOrEqualToZero) {
			return new InstructionNoOp();
		}
		else if (previous instanceof InstructionSetIsMoreThanZero) {
			return new InstructionNoOp();
		}
		else if (previous instanceof InstructionSetIsNotZero) {
			return new InstructionNoOp();
		}
		else if (previous instanceof InstructionSetIsZero) {
			return new InstructionNoOp();
		}
		else if (previous instanceof InstructionSetNegative) {
			return new InstructionConditionalJumpIfMoreThanZero(section);
		}
		else if (previous instanceof InstructionSetNot) {
			return new InstructionConditionalJumpIfMoreThanOrEqualToZero(section);
		}
		else if (previous instanceof IInstructionImmediate) {
			IInstructionImmediate immediate = (IInstructionImmediate) previous;
			Short value = immediate.getRegisterValue();
			if (value != null) {
				return value < 0 ? new InstructionJump(section) : new InstructionNoOp();
			}
		}
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.JLZ) + Helpers.toBinary(address, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JLZ + '\t' + Helpers.toHex(address, 2);
	}
}
