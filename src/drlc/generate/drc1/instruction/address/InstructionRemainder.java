package drlc.generate.drc1.instruction.address;

import drlc.Helpers;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.Instruction;

public class InstructionRemainder extends InstructionALU {
	
	public InstructionRemainder(RedstoneDataInfo info) {
		super(info);
	}
	
	@Override
	public Instruction getDataReplacement(RedstoneCode code) {
		return new InstructionRemainder(getDataInfoReplacement(code));
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.REM).concat(Helpers.toBinary(address, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.REM.concat("\t").concat(Helpers.toHex(address, 2));
	}
}
