package drlc.low.edsac.instruction.wheeler;

import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;

public class InstructionWheelerReturn extends Instruction {
	
	protected Integer address;
	
	public InstructionWheelerReturn() {
		super();
	}
	
	public void setAddress(int address) {
		if (this.address == null) {
			this.address = address;
		}
		else {
			throw new UnsupportedOperationException(String.format("Attempted to modify non-null immediate return address!"));
		}
	}
	
	@Override
	public String toAssembly() {
		return EdsacOpcodes.JUMP_IF_MORE_THAN_OR_EQUAL_TO_ZERO + EdsacCode.instructionArgument(address) + EdsacOpcodes.SHORT;
	}
}
