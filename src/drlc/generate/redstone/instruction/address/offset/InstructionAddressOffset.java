package drlc.generate.redstone.instruction.address.offset;

import drlc.generate.redstone.DataInfo;
import drlc.generate.redstone.instruction.Instruction;
import drlc.generate.redstone.instruction.address.IInstructionAddress;

public abstract class InstructionAddressOffset extends Instruction implements IInstructionAddress {
	
	public final DataInfo info;
	public Short offset;
	
	public InstructionAddressOffset(DataInfo info) {
		super();
		this.info = info;
	}
	
	@Override
	public DataInfo getDataInfo() {
		return info;
	}
}
