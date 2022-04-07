package drlc.generate.drc1.builtin;

import java.util.*;

import drlc.*;
import drlc.generate.drc1.RedstoneCode;
import drlc.generate.drc1.instruction.Instruction;
import drlc.generate.drc1.instruction.subroutine.InstructionReturnFromSubroutine;
import drlc.interpret.component.*;
import drlc.interpret.routine.RoutineType;

public class CircularLeftShiftRedstoneRoutine extends BuiltInRedstoneRoutine {
	
	public CircularLeftShiftRedstoneRoutine(RedstoneCode code, String name, RoutineType type) {
		super(code, name, type, Helpers.params(Helpers.builtInParam("x", Global.INT_TYPE_INFO), Helpers.builtInParam("y", Global.INT_TYPE_INFO)));
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		textSectionMap.put((short) 0, text);
		
		// (x << y) | (x >>> (-y))
		
		DataId x = params[0].dataId(), y = params[1].dataId();
		DataId t = nextExtraTempRegArg();
		
		unaryOp(text, UnaryOpType.MINUS, y);
		store(text, t, true);
		load(text, x);
		binaryOp(text, BinaryOpType.LOGICAL_RIGHT_SHIFT, t);
		store(text, t, true);
		load(text, x);
		binaryOp(text, BinaryOpType.ARITHMETIC_LEFT_SHIFT, y);
		binaryOp(text, BinaryOpType.OR, t);
		text.add(new InstructionReturnFromSubroutine());
	}
	
	@Override
	public short getFinalTextSectionKey() {
		throw new IllegalArgumentException(String.format("Unexpectedly attempted to get final text section key of built-in function \"%s\"!", name));
	}
}
