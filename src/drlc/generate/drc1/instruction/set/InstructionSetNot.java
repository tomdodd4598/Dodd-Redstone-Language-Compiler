package drlc.generate.drc1.instruction.set;

import drlc.Global;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.*;

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
