package drlc.generate.drc1.instruction.set;

import drlc.Global;
import drlc.generate.drc1.*;

public class InstructionSetIsMoreThanZero extends InstructionSet {
	
	public InstructionSetIsMoreThanZero() {
		super();
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDMZ).concat(Global.ZERO_8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDMZ;
	}
}
