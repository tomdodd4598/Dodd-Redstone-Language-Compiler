package drlc.low.edsac.instruction;

import java.util.Arrays;
import java.util.stream.Collectors;

import drlc.low.edsac.*;

public class InstructionRightShift extends InstructionImmediate {
	
	public final int[] shift;
	
	public InstructionRightShift(long value) {
		super(value = EdsacCode.shiftBits(value));
		
		int count = (int) (value / 15), remainder = (int) (value % 15);
		if (remainder == 0) {
			shift = new int[count];
			Arrays.fill(shift, 0, count, 15);
		}
		else if (remainder < 13) {
			shift = new int[count + 1];
			Arrays.fill(shift, 0, count, 15);
			shift[count] = remainder;
		}
		else {
			shift = new int[count + 2];
			Arrays.fill(shift, 0, count, 15);
			shift[count] = 12;
			shift[count + 1] = remainder - 12;
		}
	}
	
	@Override
	public int size() {
		return shift.length;
	}
	
	@Override
	protected char opcode() {
		return EdsacOpcodes.RIGHT_SHIFT;
	}
	
	@Override
	public String toAssembly(Integer offset) {
		return Arrays.stream(shift).mapToObj(x -> opcode() + (x == 1 ? EdsacOpcodes.LONG : (EdsacCode.instructionArgument(1 << (x - 2)) + EdsacOpcodes.SHORT))).collect(Collectors.joining("\n"));
	}
}
