package drlc.generate.redstone.instruction.set;

import drlc.Global;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;

public class InstructionSetIsLessThanZero extends InstructionSet {
	
	public InstructionSetIsLessThanZero() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDLZ).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDLZ;
	}
}
