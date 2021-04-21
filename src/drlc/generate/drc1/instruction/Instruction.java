package drlc.generate.drc1.instruction;

public abstract class Instruction {
	
	public Instruction() {}
	
	public abstract boolean isRegisterModified();
	
	public abstract boolean isRegisterExported();
	
	public boolean precedesData() {
		return false;
	}
	
	public abstract Instruction getCompressedWithNextInstruction(Instruction next);
	
	public abstract String binaryString();
	
	@Override
	public abstract String toString();
}
