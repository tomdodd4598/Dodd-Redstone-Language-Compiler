package drlc.generate.drc1.instruction;

public abstract class Instruction {
	
	public Instruction() {}
	
	public abstract boolean isCurrentRegisterValueModified();
	
	public abstract boolean isCurrentRegisterValueUsed();
	
	public boolean precedesData() {
		return false;
	}
	
	public InstructionConstant succeedingData() {
		return null;
	}
	
	/** Ignores code sectioning! */
	public abstract Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection);
	
	public abstract String binaryString();
	
	@Override
	public abstract String toString();
}
