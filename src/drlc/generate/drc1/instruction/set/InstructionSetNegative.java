package drlc.generate.drc1.instruction.set;

import drlc.Global;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.*;

public class InstructionSetNegative extends InstructionSet {
	
	public InstructionSetNegative() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDNEG).concat(Global.ZERO_8);
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		if (sameSection && next instanceof InstructionSetNegative) {
			return new InstructionNoOp();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDNEG;
	}
}
