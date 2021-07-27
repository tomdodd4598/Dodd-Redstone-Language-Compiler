package drlc.generate.drc1.instruction.address;

import drlc.generate.drc1.DataInfo;
import drlc.generate.drc1.instruction.Instruction;

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
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
}
