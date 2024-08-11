package drlc.low.drc1.instruction.set;

import drlc.Global;
import drlc.low.drc1.*;

public class InstructionSetIsZero extends InstructionSet {
	
	public InstructionSetIsZero() {
		super();
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.LDEZ) + Global.ZERO_8};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.LDEZ;
	}
}
