package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionSubtractImmediate extends InstructionALUImmediate {
	
	public InstructionSubtractImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		/*if (next instanceof InstructionAddImmediate) {
			InstructionAddImmediate add = (InstructionAddImmediate) next;
			int sum = -value + add.value;
			if (!RedstoneCode.isLongImmediate((short) sum)) {
				return new InstructionAddImmediate((short) sum);
			}
			else if (!RedstoneCode.isLongImmediate((short) -sum)) {
				return new InstructionSubtractImmediate((short) -sum);
			}
		}
		else if (next instanceof InstructionSubtractImmediate) {
			InstructionSubtractImmediate subtract = (InstructionSubtractImmediate) next;
			int sum = -value - subtract.value;
			if (!RedstoneCode.isLongImmediate((short) sum)) {
				return new InstructionAddImmediate((short) sum);
			}
			else if (!RedstoneCode.isLongImmediate((short) -sum)) {
				return new InstructionSubtractImmediate((short) -sum);
			}
		}*/
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.SUBI).concat(Helpers.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.SUBI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
