package drlc.low.drc1.instruction.immediate;

import drlc.*;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class InstructionXorImmediate extends InstructionALUImmediate {
	
	public InstructionXorImmediate(short value) {
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
		/*if (next instanceof InstructionXorImmediate) {
			InstructionXorImmediate xor = (InstructionXorImmediate) next;
			return new InstructionXorImmediate((short) (value ^ xor.value));
		}*/
		return null;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.XORI) + Helpers.toBinary(value, 8);
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.XORI + '\t' + Global.IMMEDIATE + Helpers.toHex(value);
	}
}
