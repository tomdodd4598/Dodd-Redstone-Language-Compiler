package drlc.low.edsac.instruction;

import java.util.Arrays;
import java.util.stream.Collectors;

import drlc.low.edsac.*;

public class InstructionLeftShift extends InstructionImmediate {
	
	public final int[] shift;
	
	public InstructionLeftShift(long value) {
		super(value = EdsacCode.shiftBits(value));
		
		int count = (int) (value / 13), remainder = (int) (value % 13);
		if (remainder == 0) {
			shift = new int[count];
			Arrays.fill(shift, 0, count, 13);
		}
		else {
			shift = new int[count + 1];
			Arrays.fill(shift, 0, count, 13);
			shift[count] = remainder;
		}
	}
	
	@Override
	public boolean isAccumulatorUsed() {
		return true;
	}
	
	@Override
	public boolean isAccumulatorModified() {
		return true;
	}
	
	@Override
	public Instruction getReplacement(EdsacRoutine routine) {
		return value == 0 ? new InstructionNoOp() : null;
	}
	
	@Override
	public Instruction getCompressedWithNextInstruction(EdsacRoutine routine, Instruction next, boolean sameSection) {
		return next instanceof InstructionLeftShift nextLeft ? new InstructionLeftShift(value + nextLeft.value) : null;
	}
	
	@Override
	public int size() {
		return shift.length;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.LEFT_SHIFT;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return Arrays.stream(shift).mapToObj(x -> opcode() + (x == 1 ? EdsacOpcodes.LONG : (EdsacCode.instructionArgument(1 << (x - 2)) + EdsacOpcodes.SHORT))).collect(Collectors.joining("\n"));
	}
}
