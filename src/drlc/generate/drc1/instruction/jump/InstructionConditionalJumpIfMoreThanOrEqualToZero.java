package drlc.generate.drc1.instruction.jump;

import drlc.Helper;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.set.*;

public class InstructionConditionalJumpIfMoreThanOrEqualToZero extends InstructionConditionalJump {
	
	public InstructionConditionalJumpIfMoreThanOrEqualToZero(short section) {
		super(section);
	}
	
	@Override
	public Instruction getReplacementConditionalJump(InstructionSet instructionSet) {
		if (instructionSet instanceof InstructionSetIsLessThanOrEqualToZero) {
			return new InstructionJump(section);
		}
		else if (instructionSet instanceof InstructionSetIsLessThanZero) {
			return new InstructionJump(section);
		}
		else if (instructionSet instanceof InstructionSetIsMoreThanOrEqualToZero) {
			return new InstructionJump(section);
		}
		else if (instructionSet instanceof InstructionSetIsMoreThanZero) {
			return new InstructionJump(section);
		}
		else if (instructionSet instanceof InstructionSetIsNotZero) {
			return new InstructionJump(section);
		}
		else if (instructionSet instanceof InstructionSetIsZero) {
			return new InstructionJump(section);
		}
		else if (instructionSet instanceof InstructionSetNegative) {
			return new InstructionConditionalJumpIfLessThanOrEqualToZero(section);
		}
		else if (instructionSet instanceof InstructionSetNot) {
			return new InstructionConditionalJumpIfLessThanZero(section);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.JMEZ).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JMEZ.concat("\t").concat(Helper.toHex(address, 2));
	}
}
