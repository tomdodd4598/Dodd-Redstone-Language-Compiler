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

public class InstructionConditionalJumpIfLessThanOrEqualToZero extends InstructionConditionalJump {
	
	public InstructionConditionalJumpIfLessThanOrEqualToZero(short section) {
		super(section);
	}
	
	@Override
	public Instruction getReplacementConditionalJump(InstructionSet instructionSet) {
		if (instructionSet instanceof InstructionSetIsLessThanOrEqualToZero) {
			return new InstructionConditionalJumpIfMoreThanZero(section);
		}
		else if (instructionSet instanceof InstructionSetIsLessThanZero) {
			return new InstructionConditionalJumpIfMoreThanOrEqualToZero(section);
		}
		else if (instructionSet instanceof InstructionSetIsMoreThanOrEqualToZero) {
			return new InstructionConditionalJumpIfLessThanZero(section);
		}
		else if (instructionSet instanceof InstructionSetIsMoreThanZero) {
			return new InstructionConditionalJumpIfLessThanOrEqualToZero(section);
		}
		else if (instructionSet instanceof InstructionSetIsNotZero) {
			return new InstructionConditionalJumpIfZero(section);
		}
		else if (instructionSet instanceof InstructionSetIsZero) {
			return new InstructionConditionalJumpIfNotZero(section);
		}
		else if (instructionSet instanceof InstructionSetNegative) {
			return new InstructionConditionalJumpIfMoreThanOrEqualToZero(section);
		}
		else if (instructionSet instanceof InstructionSetNot) {
			return null;
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.JLEZ).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JLEZ.concat("\t").concat(Helper.toHex(address, 2));
	}
}
