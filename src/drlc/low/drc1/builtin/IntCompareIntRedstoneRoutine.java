package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.jump.InstructionConditionalJumpIfNotZero;
import drlc.low.drc1.instruction.set.InstructionSetIsMoreThanOrEqualToZero;

public class IntCompareIntRedstoneRoutine extends RedstoneRoutine {
	
	public IntCompareIntRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> ifText = new ArrayList<>(), elseText = new ArrayList<>();
		sectionTextMap.put(0, ifText);
		sectionTextMap.put(1, elseText);
		
		DataId x = params.get(0).dataId(), y = params.get(1).dataId();
		DataId a = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		DataId b = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		
		loadScalar(ifText, x);
		ifText.add(new InstructionSetIsMoreThanOrEqualToZero());
		storeScalar(ifText, a);
		
		loadScalar(ifText, y);
		ifText.add(new InstructionSetIsMoreThanOrEqualToZero());
		storeScalar(ifText, b);
		
		binaryOp(ifText, BinaryActionType.INT_XOR_INT, a);
		ifText.add(new InstructionConditionalJumpIfNotZero(1));
		
		loadScalar(ifText, x);
		binaryOp(ifText, BinaryActionType.INT_MINUS_INT, y);
		returnFromSubroutine(ifText);
		
		loadScalar(elseText, a);
		binaryOp(elseText, BinaryActionType.INT_MINUS_INT, b);
		returnFromSubroutine(elseText);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 2;
	}
}
