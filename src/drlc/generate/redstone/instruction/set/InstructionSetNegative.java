package drlc.generate.redstone.instruction.set;

import drlc.Global;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;

public class InstructionSetNegative extends InstructionSet {
	
	public InstructionSetNegative() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDNEG).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDNEG;
	}
}
