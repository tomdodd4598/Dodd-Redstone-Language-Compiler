package drlc.low.drc1.instruction.subroutine;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;

public class InstructionSubtractFromStackPointer extends Instruction {
	
	public Short value;
	
	public InstructionSubtractFromStackPointer() {
		super();
	}
	
	public InstructionSubtractFromStackPointer(short value) {
		super();
		this.value = value;
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return false;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		if (sameSection && value != null) {
			if (next instanceof InstructionAddToStackPointer add) {
				if (add.value != null) {
					int sum = -value + add.value;
					if (sum == 0) {
						return new InstructionNoOp();
					}
					else if (!RedstoneCode.isLong((short) sum)) {
						return new InstructionAddToStackPointer((short) sum);
					}
					else if (!RedstoneCode.isLong((short) -sum)) {
						return new InstructionSubtractFromStackPointer((short) -sum);
					}
				}
			}
			else if (next instanceof InstructionSubtractFromStackPointer subtract) {
				if (subtract.value != null) {
					int sum = -value - subtract.value;
					if (sum == 0) {
						return new InstructionNoOp();
					}
					else if (!RedstoneCode.isLong((short) sum)) {
						return new InstructionAddToStackPointer((short) sum);
					}
					else if (!RedstoneCode.isLong((short) -sum)) {
						return new InstructionSubtractFromStackPointer((short) -sum);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public int size(boolean longAddress) {
		return 1;
	}
	
	@Override
	public String[] toBinary(boolean longAddress) {
		return new String[] {RedstoneOpcodes.get(RedstoneMnemonics.SUBSP) + Helpers.toBinary(value, 8)};
	}
	
	@Override
	public String toAssembly(boolean longAddress) {
		return RedstoneMnemonics.SUBSP + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
