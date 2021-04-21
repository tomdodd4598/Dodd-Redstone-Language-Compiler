package drlc.generate.drc1.instruction.address.offset;

import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.address.IInstructionAddress;

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
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		return null;
	}
}
