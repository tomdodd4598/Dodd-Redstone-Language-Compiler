package drlc.generate.redstone.instruction.set;

import drlc.Global;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;

public class InstructionSetIsLessThanOrEqualToZero extends InstructionSet {
	
	public InstructionSetIsLessThanOrEqualToZero() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDLEZ).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDLEZ;
	}
}
