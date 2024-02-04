package drlc.low.drc1.instruction.address.offset;

import drlc.low.LowDataInfo;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.address.IInstructionAddress;

public abstract class InstructionAddressOffset extends Instruction implements IInstructionAddress {
	
	public final LowDataInfo info;
	public Short offset;
	
	public InstructionAddressOffset(LowDataInfo info) {
		super();
		this.info = info;
	}
	
	@Override
	public LowDataInfo getDataInfo() {
		return info;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
}
