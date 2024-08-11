package drlc.low.drc1.instruction;

public abstract class Instruction {
	
	public Instruction() {}
	
	public abstract boolean isCurrentRegisterValueModified();
	
	public abstract boolean isCurrentRegisterValueUsed();
	
	/** Ignores code sectioning! */
	public abstract Instruction getCompressedWithNextInstruction(Instruction next, boolean sameSection);
	
	public abstract int size(boolean longAddress);
	
	public abstract String[] toBinary(boolean longAddress);
	
	public abstract String toAssembly(boolean longAddress);
	
	@Override
	public final String toString() {
		return toAssembly(true);
	}
}
