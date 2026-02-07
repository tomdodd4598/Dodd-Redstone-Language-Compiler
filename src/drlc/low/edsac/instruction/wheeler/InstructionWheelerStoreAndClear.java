package drlc.low.edsac.instruction.wheeler;

import drlc.intermediate.component.Function;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionWheelerStoreAndClear extends Instruction {
	
	public final Function function;
	public final int section;
	public final int offset;
	public Integer address;
	
	public InstructionWheelerStoreAndClear(Function function, int section, int offset) {
		this.function = function;
		this.section = section;
		this.offset = offset;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return EdsacOpcodes.STORE_AND_CLEAR + EdsacCode.instructionArgument(address) + suffix(EdsacOpcodes.SHORT, offset);
	}
}
