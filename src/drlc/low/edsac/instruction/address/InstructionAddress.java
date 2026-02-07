package drlc.low.edsac.instruction.address;

import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;
import drlc.low.instruction.address.IInstructionAddress;

public abstract class InstructionAddress extends Instruction implements IInstructionAddress {
	
	public LowDataInfo dataInfo;
	public Integer address;
	
	protected InstructionAddress(LowDataInfo dataInfo) {
		super();
		this.dataInfo = dataInfo;
	}
	
	@Override
	public LowDataInfo getDataInfo() {
		return dataInfo;
	}
	
	@Override
	public void regenerateDataInfo() {
		dataInfo = dataInfo.getRegeneratedDataInfo();
	}
	
	protected abstract String opcode();
	
	@Override
	public String toAssembly(Integer offset) {
		return opcode() + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
