package drlc.generate.drc1.instruction.immediate;

import drlc.Global;
import drlc.Helper;
import drlc.generate.drc1.RedstoneMnemonics;
import drlc.generate.drc1.RedstoneOpcodes;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionAddImmediate extends InstructionALUImmediate {
	
	public InstructionAddImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isUnnecessaryImmediate() {
		return value == 0;
	}
	
	@Override
	public Instruction getALUImmediateReplacementInternal() {
		return null;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next) {
		/*if (next instanceof InstructionAddImmediate) {
			InstructionAddImmediate add = (InstructionAddImmediate) next;
			int sum = value + add.value;
			if (!RedstoneCode.isLongImmediate((short) sum)) {
				return new InstructionAddImmediate((short) sum);
			}
			else if (!RedstoneCode.isLongImmediate((short) -sum)) {
				return new InstructionSubtractImmediate((short) -sum);
			}
		}
		else if (next instanceof InstructionSubtractImmediate) {
			InstructionSubtractImmediate subtract = (InstructionSubtractImmediate) next;
			int sum = value - subtract.value;
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
		return RedstoneOpcodes.get(RedstoneMnemonics.ADDI).concat(Helper.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ADDI.concat("\t").concat(Global.IMMEDIATE).concat(Helper.toHex(value));
	}
}
