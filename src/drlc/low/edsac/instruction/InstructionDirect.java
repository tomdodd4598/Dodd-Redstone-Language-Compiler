package drlc.low.edsac.instruction;

public class InstructionDirect extends Instruction {
	
	protected final String str;
	
	public InstructionDirect(String str) {
		super();
		this.str = str;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return str;
	}
}
