package drlc.generate.drc1.instruction.immediate;

public abstract class InstructionALUImmediate extends InstructionImmediate {
	
	public InstructionALUImmediate(short value) {
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
