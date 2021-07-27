package drlc.generate.drc1.instruction.immediate;

public abstract class InstructionALULongImmediate extends InstructionLongImmediate {
	
	public InstructionALULongImmediate(short value) {
		super(value);
	}
	
	@Override
	public boolean isCurrentRegisterValueModified() {
		return true;
	}
	
	@Override
	public boolean isCurrentRegisterValueUsed() {
		return false;
	}
	
	@Override
	public Short getRegisterValue() {
		return null;
	}
}
