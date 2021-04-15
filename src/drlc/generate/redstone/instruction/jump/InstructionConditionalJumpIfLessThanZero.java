package drlc.generate.redstone.instruction.jump;

import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.InstructionNoOp;
import drlc.generate.redstone.instruction.set.InstructionSet;
import drlc.generate.redstone.instruction.set.InstructionSetIsLessThanOrEqualToZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsLessThanZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsMoreThanOrEqualToZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsMoreThanZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsNotZero;
import drlc.generate.redstone.instruction.set.InstructionSetIsZero;
import drlc.generate.redstone.instruction.set.InstructionSetNegative;
import drlc.generate.redstone.instruction.set.InstructionSetNot;

public class InstructionConditionalJumpIfLessThanZero extends InstructionConditionalJump {
	
	public InstructionConditionalJumpIfLessThanZero(short section) {
		super(section);
	}
	
	@Override
	public Instruction getReplacementConditionalJump(InstructionSet instructionSet) {
		if (instructionSet instanceof InstructionSetIsLessThanOrEqualToZero) {
			return new InstructionNoOp();
		}
		else if (instructionSet instanceof InstructionSetIsLessThanZero) {
			return new InstructionNoOp();
		}
		else if (instructionSet instanceof InstructionSetIsMoreThanOrEqualToZero) {
			return new InstructionNoOp();
		}
		else if (instructionSet instanceof InstructionSetIsMoreThanZero) {
			return new InstructionNoOp();
		}
		else if (instructionSet instanceof InstructionSetIsNotZero) {
			return new InstructionNoOp();
		}
		else if (instructionSet instanceof InstructionSetIsZero) {
			return new InstructionNoOp();
		}
		else if (instructionSet instanceof InstructionSetNegative) {
			return new InstructionConditionalJumpIfMoreThanZero(section);
		}
		else if (instructionSet instanceof InstructionSetNot) {
			return new InstructionConditionalJumpIfMoreThanOrEqualToZero(section);
		}
		else {
			return null;
		}
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.JLZ).concat(Helper.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.JLZ.concat("\t").concat(Helper.toHex(address, 2));
	}
}
