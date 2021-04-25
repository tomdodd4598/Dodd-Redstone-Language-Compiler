package drlc.generate.drc1.instruction.jump;

import drlc.Helper;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.*;
import drlc.generate.drc1.instruction.set.*;

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
