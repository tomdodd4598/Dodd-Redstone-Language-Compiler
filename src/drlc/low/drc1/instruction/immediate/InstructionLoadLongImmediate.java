package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionLoadLongImmediate extends InstructionLongImmediate implements IInstructionLoadImmediate {
	
	public InstructionLoadLongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return false;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		short complement = (short) ~value;
		if (!RedstoneCode.isLongImmediate(complement)) {
			return new InstructionNotImmediate(complement);
		}
		else {
			return null;
		}
	}
	
	@Override
	public Short getRegisterValue() {
		return value;
	}
	
	@Override
	public short getLoadedValue() {
		return value;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.LDALI) + Global.ZERO_8;
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.LDALI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
