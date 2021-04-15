package drlc.generate.redstone.instruction.jump;

import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.set.InstructionSet;
import drlc.generate.redstone.instruction.set.InstructionSetIsLessThanOrEqualToZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsLessThanZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsMoreThanOrEqualToZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsMoreThanZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsNotZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsZero;
import drlc.generate.redstone.instruction.set.InstructionSetNegative;
import drlc.generate.redstone.instruction.set.InstructionSetNot;

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
