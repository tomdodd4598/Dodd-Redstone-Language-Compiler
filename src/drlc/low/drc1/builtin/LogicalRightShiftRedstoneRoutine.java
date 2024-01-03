package drlc.low.drc1.builtin;

import java.util.*;

import drlc.*;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.RoutineCallType;
import drlc.low.drc1.RedstoneCode;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.immediate.InstructionLeftShiftImmediate;
import drlc.low.drc1.instruction.set.InstructionSetNot;
import drlc.low.drc1.instruction.subroutine.InstructionReturnFromSubroutine;

public class LogicalRightShiftRedstoneRoutine extends BuiltInRedstoneRoutine {
	
	public LogicalRightShiftRedstoneRoutine(RedstoneCode code, String name) {
		super(code, name, RoutineCallType.LEAF, Helpers.params(Helpers.builtInDeclarator("x", Global.INT_TYPE_INFO), Helpers.builtInDeclarator("y", Global.INT_TYPE_INFO)));
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
