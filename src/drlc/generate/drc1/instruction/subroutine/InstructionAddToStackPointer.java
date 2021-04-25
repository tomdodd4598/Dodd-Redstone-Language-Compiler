package drlc.generate.drc1.instruction.subroutine;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

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
	public boolean isRegisterModified() {
		return false;
	}
	
	@Override
	public boolean isRegisterExported() {
		return false;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		if (value != null) {
			if (next instanceof InstructionAddToStackPointer) {
				InstructionAddToStackPointer add = (InstructionAddToStackPointer) next;
				if (add.value != null) {
					int sum = value + add.value;
					if (!RedstoneCode.isLongImmediate((short) sum)) {
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
					if (!RedstoneCode.isLongImmediate((short) sum)) {
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
		return RedstoneOpcodes.get(RedstoneMnemonics.ADDSP).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ADDSP.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
