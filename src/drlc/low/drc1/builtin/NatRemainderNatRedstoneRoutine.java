package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.jump.InstructionConditionalJumpIfZero;

public class NatRemainderNatRedstoneRoutine extends RedstoneRoutine {
	
	public NatRemainderNatRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> setupText = new ArrayList<>(), ifText = new ArrayList<>(), ifSubtractText = new ArrayList<>(), elseText = new ArrayList<>(), elseSubtractText = new ArrayList<>();
		sectionTextMap.put(0, setupText);
		sectionTextMap.put(1, ifText);
		sectionTextMap.put(2, ifSubtractText);
		sectionTextMap.put(3, elseText);
		sectionTextMap.put(4, elseSubtractText);
		
		DataId x = params.get(0).dataId(), y = params.get(1).dataId();
		DataId t = function.scope.nextLocalDataId(intermediate, x.typeInfo);
		
		loadScalar(setupText, y);
		setupText.add(new InstructionAndImmediate((short) 32768));
		setupText.add(new InstructionConditionalJumpIfZero(3));
		
		loadScalar(ifText, x);
		binaryOp(ifText, BinaryActionType.NAT_LESS_THAN_NAT, y);
		ifText.add(new InstructionConditionalJumpIfZero(2));
		loadScalar(ifText, x);
		returnFromSubroutine(ifText);
		
		loadScalar(ifSubtractText, x);
		binaryOp(ifSubtractText, BinaryActionType.INT_MINUS_INT, y);
		returnFromSubroutine(ifSubtractText);
		
		loadScalar(elseText, x);
		elseText.add(new InstructionRightShiftImmediate((short) 1));
		elseText.add(new InstructionAndImmediate((short) 32767));
		binaryOp(elseText, BinaryActionType.INT_REMAINDER_INT, y);
		elseText.add(new InstructionLeftShiftImmediate((short) 1));
		storeScalar(elseText, t);
		
		loadScalar(elseText, x);
		elseText.add(new InstructionAndImmediate((short) 1));
		binaryOp(elseText, BinaryActionType.INT_OR_INT, t);
		storeScalar(elseText, t);
		
		loadScalar(elseText, t);
		binaryOp(elseText, BinaryActionType.NAT_LESS_THAN_NAT, y);
		elseText.add(new InstructionConditionalJumpIfZero(4));
		loadScalar(elseText, t);
		returnFromSubroutine(elseText);
		
		loadScalar(elseSubtractText, t);
		binaryOp(elseSubtractText, BinaryActionType.INT_MINUS_INT, y);
		returnFromSubroutine(elseSubtractText);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 5;
	}
}
