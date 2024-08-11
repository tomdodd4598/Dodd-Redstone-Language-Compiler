package drlc.low.drc1.instruction.set;

import drlc.Global;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;

public class InstructionSetNot extends InstructionSet {
	
	public InstructionSetNot() {
		super();
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		if (sameSection && next instanceof InstructionSetNot) {
			return new InstructionNoOp();
		}
		else {
			return null;
		}
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.LDNOT) + Global.ZERO_8};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.LDNOT;
	}
}
