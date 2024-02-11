package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.action.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class IntRightRotateIntRedstoneRoutine extends RedstoneRoutine {
	
	public IntRightRotateIntRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		textSectionMap.put(0, text);
		
		// (x >>> y) | (x << (-y))
		
		DataId x = params.get(0).dataId(), y = params.get(1).dataId();
		DataId t = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		
		unaryOp(text, UnaryActionType.MINUS_INT, y);
		storeScalar(text, t);
		
		loadScalar(text, x);
		binaryOp(text, BinaryActionType.INT_LEFT_SHIFT_INT, t);
		storeScalar(text, t);
		
		loadScalar(text, x);
		binaryOp(text, BinaryActionType.NAT_RIGHT_SHIFT_INT, y);
		
		binaryOp(text, BinaryActionType.INT_OR_INT, t);
		
		returnFromSubroutine(text);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 1;
	}
}
