package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;

public abstract class InstructionImmediate extends Instruction implements IInstructionImmediate {
	
	public final short value;
	
	public InstructionImmediate(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
	}
	
	@Override
	public Instruction getImmediateReplacement() {
		if (isUnnecessaryImmediate()) {
			return new InstructionNoOp();
		}
		else {
			return getImmediateReplacementInternal();
		}
	}
	
	protected abstract Instruction getImmediateReplacementInternal();
	
	@Override
	public int size(boolean longAddress) {
		return RedstoneCode.isLongImmediate(value) ? 2 : 1;
	}
	
	protected String[] toBinary(boolean longAddress, String mnemonic, String longMnemonic) {
		if (RedstoneCode.isLongImmediate(value)) {
			return new String[] {RedstoneOpcodes.get(longMnemonic) + Global.ZERO_8, Helpers.toBinary(value, 16)};
		}
		else {
			return new String[] {RedstoneOpcodes.get(mnemonic) + Helpers.toBinary(value, 8)};
		}
	}
	
	protected String toAssembly(boolean longAddress, String mnemonic, String longMnemonic) {
		if (RedstoneCode.isLongImmediate(value)) {
			return longMnemonic + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
		}
		else {
			return mnemonic + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
		}
	}
}
