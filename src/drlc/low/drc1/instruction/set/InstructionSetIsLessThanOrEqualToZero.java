package drlc.low.drc1.instruction.set;

import drlc.Global;
import drlc.low.drc1.*;

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
