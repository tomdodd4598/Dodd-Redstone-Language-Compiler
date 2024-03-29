package drlc.low.drc1.instruction.set;

import drlc.Global;
import drlc.low.drc1.*;

public class InstructionSetIsMoreThanOrEqualToZero extends InstructionSet {
	
	public InstructionSetIsMoreThanOrEqualToZero() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDMEZ) + Global.ZERO_8;
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDMEZ;
	}
}
