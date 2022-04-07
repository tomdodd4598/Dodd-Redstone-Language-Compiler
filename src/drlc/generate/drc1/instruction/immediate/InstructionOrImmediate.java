package drlc.generate.drc1.instruction.immediate;

import drlc.*;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionOrImmediate extends InstructionALUImmediate {
	
	public InstructionOrImmediate(short value) {
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
		/*if (next instanceof InstructionOrImmediate) {
			InstructionOrImmediate or = (InstructionOrImmediate) next;
			return new InstructionOrImmediate((short) (value | or.value));
		}*/
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.ORI).concat(Helpers.toBinary(value, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.ORI.concat("\t").concat(Global.IMMEDIATE).concat(Helpers.toHex(value));
	}
}
