package drlc.generate.drc1.instruction.set;

import drlc.Global;
import drlc.generate.drc1.*;

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
