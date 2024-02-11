package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.jump.*;
import drlc.low.drc1.instruction.set.InstructionSetIsZero;

public class PrintDigitsRedstoneRoutine extends RedstoneRoutine {
	
	public PrintDigitsRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> setupText = new ArrayList<>(), loopText = new ArrayList<>(), returnText = new ArrayList<>();
		textSectionMap.put(0, setupText);
		textSectionMap.put(1, loopText);
		textSectionMap.put(2, returnText);
		
		DataId x = params.get(0).dataId(), t = params.get(1).dataId(), b = params.get(2).dataId();
		DataId d = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		DataId m = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		
		loadScalar(loopText, x);
		binaryOp(loopText, BinaryActionType.INT_DIVIDE_INT, t);
		storeScalar(loopText, d);
		
		binaryOp(loopText, BinaryActionType.INT_MULTIPLY_INT, t);
		storeScalar(loopText, m);
		loadScalar(loopText, x);
		binaryOp(loopText, BinaryActionType.INT_MINUS_INT, m);
		storeScalar(loopText, x);
		
		loadScalar(loopText, t);
		loopText.add(new InstructionDivideImmediate((short) 10));
		storeScalar(loopText, t);
		
		loopText.add(new InstructionSetIsZero());
		binaryOp(loopText, BinaryActionType.INT_OR_INT, d);
		binaryOp(loopText, BinaryActionType.INT_OR_INT, b);
		storeScalar(loopText, b);
		loopText.add(new InstructionConditionalJumpIfZero(2));
		
		loadScalar(loopText, d);
		loopText.add(new InstructionAddImmediate((short) 48));
		loopText.add(new InstructionOutput());
		
		loadScalar(returnText, t);
		returnText.add(new InstructionConditionalJumpIfMoreThanZero(1));
		
		returnFromSubroutine(returnText);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 3;
	}
}
