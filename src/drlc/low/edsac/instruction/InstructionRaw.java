package drlc.low.edsac.instruction;

public class InstructionRaw extends Instruction {
	
	protected final String str;
	
	public InstructionRaw(String str) {
		super();
		this.str = str;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return str;
	}
}
