package drlc.low.drc1.instruction.address.offset;

import drlc.Helpers;
import drlc.low.LowDataInfo;
import drlc.low.drc1.RedstoneOpcodes;
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
	
	@Override
	public int size(boolean longAddress) {
		return 1;
	}
	
	public String[] toBinary(boolean longAddress, String positiveMnemonic, String negativeMnemonic) {
		if (offset < 0) {
			return new String[] {RedstoneOpcodes.get(negativeMnemonic) + Helpers.toBinary(-offset, 8)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(positiveMnemonic) + Helpers.toBinary(offset, 8)};
		}
	}
	
	public String toAssembly(boolean longAddress, String positiveMnemonic, String negativeMnemonic) {
		if (offset < 0) {
			return negativeMnemonic + '\t' + Helpers.toHex(-offset, 2);
		}
		else {
			return positiveMnemonic + '\t' + Helpers.toHex(offset, 2);
		}
	}
}
