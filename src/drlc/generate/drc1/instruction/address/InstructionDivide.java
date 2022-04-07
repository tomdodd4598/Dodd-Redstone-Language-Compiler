package drlc.generate.drc1.instruction.address;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionDivide extends InstructionALU {
	
	public InstructionDivide(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionDivide(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.DIV).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.DIV.concat("\t").concat(Helpers.toHex(address, 2));
	}
}
