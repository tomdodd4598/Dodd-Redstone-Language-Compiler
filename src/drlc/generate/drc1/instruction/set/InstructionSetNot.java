package drlc.generate.drc1.instruction.set;

import drlc.Global;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.InstructionNoOp;

public class InstructionSetNot extends InstructionSet {
	
	public InstructionSetNot() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDNOT).concat(Global.ZERO_8);
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		if (next instanceof InstructionSetNot) {
			return new InstructionNoOp();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDNOT;
	}
}
