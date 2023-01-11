package drlc.low.drc1.instruction.address.offset;

import drlc.low.drc1.RedstoneDataInfo;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.address.IInstructionAddress;

public abstract class InstructionAddressOffset extends Instruction implements IInstructionAddress {
	
	public final RedstoneDataInfo info;
	public Short offset;
	
	public InstructionAddressOffset(RedstoneDataInfo info) {
		super();
		this.info = info;
	}
	
	@Override
	public RedstoneDataInfo getDataInfo() {
		return info;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
}
