package drlc.low.drc1.instruction.subroutine;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;

public class InstructionAddToStackPointer extends Instruction {
	
	public Short value;
	
	public InstructionAddToStackPointer() {
		super();
	}
	
	public InstructionAddToStackPointer(short value) {
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
			if (next instanceof InstructionAddToStackPointer) {
				InstructionAddToStackPointer add = (InstructionAddToStackPointer) next;
				if (add.value != null) {
					int sum = value + add.value;
					if (sum == 0) {
						return new InstructionNoOp();
					}
					else if (!RedstoneCode.isLongImmediate((short) sum)) {
						return new InstructionAddToStackPointer((short) sum);
					}
					else if (!RedstoneCode.isLongImmediate((short) -sum)) {
						return new InstructionSubtractFromStackPointer((short) -sum);
					}
				}
			}
			else if (next instanceof InstructionSubtractFromStackPointer) {
				InstructionSubtractFromStackPointer subtract = (InstructionSubtractFromStackPointer) next;
				if (subtract.value != null) {
					int sum = value - subtract.value;
					if (sum == 0) {
						return new InstructionNoOp();
					}
					else if (!RedstoneCode.isLongImmediate((short) sum)) {
						return new InstructionAddToStackPointer((short) sum);
					}
					else if (!RedstoneCode.isLongImmediate((short) -sum)) {
						return new InstructionSubtractFromStackPointer((short) -sum);
					}
				}
			}
		}
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.ADDSP) + Helpers.toBinary(value, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ADDSP + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
