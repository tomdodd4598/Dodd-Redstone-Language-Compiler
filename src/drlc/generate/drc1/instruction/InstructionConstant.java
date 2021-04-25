package drlc.generate.drc1.instruction;

import drlc.*;

public class InstructionConstant extends Instruction {
	
	protected Short value;
	protected String valueString;
	
	public InstructionConstant() {
		super();
	}
	
	public InstructionConstant(short value) {
		super();
		setValue(value);
	}
	
	public void setValue(short value) {
		if (this.value == null) {
			this.value = value;
			valueString = Helper.toBinary(value, 16);
		}
		else {
			throw new UnsupportedOperationException(String.format("Attempted to modify non-null constant value!"));
		}
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
		return null;
	}
	
	@Override
	public String binaryString() {
		return valueString;
	}
	
	@Override
	public String toString() {
		return Global.IMMEDIATE.concat(Helper.toHex(value));
	}
}
