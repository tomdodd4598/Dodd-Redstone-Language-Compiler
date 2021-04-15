package drlc.generate.redstone.instruction;

public abstract class Instruction {
	
	public Instruction() {}
	
	public abstract boolean isRegisterModified();
	
	public abstract boolean isRegisterExported();
	
	public boolean precedesData() {
		return false;
	}
	
	public abstract String binaryString();
	
	@Override
	public abstract String toString();
}
