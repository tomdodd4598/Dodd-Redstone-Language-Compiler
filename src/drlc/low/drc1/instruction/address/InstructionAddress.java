package drlc.low.drc1.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.drc1.instruction.Instruction;

public abstract class InstructionAddress extends Instruction implements IInstructionAddress {
	
	public final LowDataInfo info;
	public Short address;
	
	public InstructionAddress(LowDataInfo info) {
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
