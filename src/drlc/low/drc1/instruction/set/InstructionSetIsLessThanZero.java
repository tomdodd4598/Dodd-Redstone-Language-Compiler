package drlc.low.drc1.instruction.set;

import drlc.Global;
import drlc.low.drc1.*;

public class InstructionSetIsLessThanZero extends InstructionSet {
	
	public InstructionSetIsLessThanZero() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDLZ) + Global.ZERO_8;
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDLZ;
	}
}
