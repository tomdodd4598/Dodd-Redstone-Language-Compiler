package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.RedstoneDataInfo;
import drlc.generate.drc1.instruction.Instruction;

public abstract class InstructionAddress extends Instruction implements IInstructionAddress {
	
	public final RedstoneDataInfo info;
	public Short address;
	
	public InstructionAddress(RedstoneDataInfo info) {
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
