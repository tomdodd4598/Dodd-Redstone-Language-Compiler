package drlc.generate.drc1.builtin;

import java.util.*;

import drlc.*;
import drlc.generate.drc1.RedstoneCode;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.immediate.InstructionLeftShiftImmediate;
import drlc.generate.drc1.instruction.set.InstructionSetNot;
import drlc.generate.drc1.instruction.subroutine.InstructionReturnFromSubroutine;
import drlc.interpret.component.*;
import drlc.interpret.routine.RoutineType;

public class LogicalRightShiftRedstoneRoutine extends BuiltInRedstoneRoutine {
	
	public LogicalRightShiftRedstoneRoutine(RedstoneCode code, String name) {
		super(code, name, RoutineType.LEAF, Helpers.params(Helpers.builtInParam("x", Global.INT_TYPE_INFO), Helpers.builtInParam("y", Global.INT_TYPE_INFO)));
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		textSectionMap.put((short) 0, text);
		
		// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
		
		DataId x = params[0].dataId(), y = params[1].dataId();
		
		load(text, x);
		binaryOp(text, BinaryOpType.ARITHMETIC_RIGHT_SHIFT, y);
		store(text, x, true);
		text.add(RedstoneCode.LOAD_MIN_VALUE);
		text.add(RedstoneCode.LOAD_MIN_VALUE_SUCCEEDING);
		binaryOp(text, BinaryOpType.ARITHMETIC_RIGHT_SHIFT, y);
		text.add(new InstructionLeftShiftImmediate((short) 1));
		text.add(new InstructionSetNot());
		binaryOp(text, BinaryOpType.AND, x);
		text.add(new InstructionReturnFromSubroutine());
	}
	
	@Override
	public short getFinalTextSectionKey() {
		throw new IllegalArgumentException(String.format("Unexpectedly attempted to get final text section key of built-in function \"%s\"!", name));
	}
}
