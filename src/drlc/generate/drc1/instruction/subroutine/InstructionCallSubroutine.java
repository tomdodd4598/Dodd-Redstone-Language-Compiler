package drlc.generate.drc1.instruction.subroutine;

import drlc.Helper;
import drlc.generate.drc1.*;
import drlc.generate.drc1.instruction.*;

public class InstructionCallSubroutine extends Instruction {
	
	public final String subroutine;
	public Short returnAddress, callAddress;
	
	public InstructionCallSubroutine(String subroutine) {
		super();
		this.subroutine = subroutine;
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
	public boolean precedesData() {
		return true;
	}
	
	@Override
	public InstructionConstant succeedingData() {
		if (callAddress == null) {
			return new InstructionConstant();
		}
		else {
			return new InstructionConstant(callAddress);
		}
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection) {
		return null;
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
