package drlc.generate.redstone.instruction.address;

import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.instruction.Instruction;

public abstract class InstructionAddress extends Instruction implements IInstructionAddress {
	
	public final DataInfo info;
	public Short address;
	
	public InstructionAddress(DataInfo info) {
		super();
		this.info = info;
	}
	
	@Override
	public DataInfo getDataInfo() {
		return info;
	}
}
