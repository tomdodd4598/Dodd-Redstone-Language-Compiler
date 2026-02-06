package drlc.low.edsac.instruction.wheeler;

import drlc.intermediate.component.Function;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionWheelerJump extends Instruction {
	
	public final Function function;
	public final InstructionWheelerReturn iwr;
	public Integer address;
	
	public InstructionWheelerJump(Function function, InstructionWheelerReturn iwr) {
		this.function = function;
		this.iwr = iwr;
	}
	
	@Override
	public String toAssembly() {
		return EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO + EdsacCode.instructionArgument(address) + EdsacOpcodes.SHORT;
	}
}
