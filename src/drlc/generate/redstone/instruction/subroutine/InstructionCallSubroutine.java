package drlc.generate.redstone.instruction.subroutine;

import drlc.Helper;
import drlc.generate.redstone.RedstoneMnemonics;
import drlc.generate.redstone.RedstoneOpcodes;
import drlc.generate.redstone.instruction.Instruction;

public class InstructionCallSubroutine extends Instruction {
	
	public final String subroutine;
	public Short returnAddress, callAddress;
	
	public InstructionCallSubroutine(String subroutine) {
		super();
		this.subroutine = subroutine;
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
	public boolean precedesData() {
		return true;
	}
	
	@Override
	public String binaryString() {
		return RedstoneOpcodes.get(RedstoneMnemonics.CALL).concat(Helper.toBinary(returnAddress, 8));
	}
	
	@Override
	public String toString() {
		return RedstoneMnemonics.CALL.concat("\t").concat(Helper.toHex(callAddress, 2));
	}
}
