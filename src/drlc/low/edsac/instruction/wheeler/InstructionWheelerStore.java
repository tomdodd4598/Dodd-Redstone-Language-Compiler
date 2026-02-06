package drlc.low.edsac.instruction.wheeler;

import drlc.intermediate.component.Function;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionWheelerStore extends Instruction {
	
	public final Function function;
	public final int section;
	public final int offset;
	public Integer address;
	
	public InstructionWheelerStore(Function function, int section, int offset) {
		this.function = function;
		this.section = section;
		this.offset = offset;
	}
	
	@Override
	public String toAssembly() {
		return EdsacOpcodes.STORE + EdsacCode.instructionArgument(address) + EdsacOpcodes.SHORT;
	}
}
