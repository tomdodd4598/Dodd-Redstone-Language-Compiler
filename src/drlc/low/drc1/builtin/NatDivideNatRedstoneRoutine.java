package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.immediate.*;
import drlc.low.drc1.instruction.jump.InstructionConditionalJumpIfZero;

public class NatDivideNatRedstoneRoutine extends RedstoneRoutine {
	
	public NatDivideNatRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> setupText = new ArrayList<>(), ifText = new ArrayList<>(), elseText = new ArrayList<>();
		sectionTextMap.put(0, setupText);
		sectionTextMap.put(1, ifText);
		sectionTextMap.put(2, elseText);
		
		DataId x = params.get(0).dataId(), y = params.get(1).dataId();
		DataId a = function.functionScope.nextLocalDataId(intermediate, x.typeInfo);
		DataId b = function.functionScope.nextLocalDataId(intermediate, x.typeInfo);
		
		loadScalar(setupText, y);
		setupText.add(new InstructionAndImmediate((short) 32768));
		setupText.add(new InstructionConditionalJumpIfZero(2));
		
		loadScalar(ifText, x);
		binaryOp(ifText, BinaryActionType.NAT_MORE_OR_EQUAL_NAT, y);
		returnFromSubroutine(ifText);
		
		loadScalar(elseText, x);
		elseText.add(new InstructionRightShiftImmediate((short) 1));
		elseText.add(new InstructionAndImmediate((short) 32767));
		storeScalar(elseText, a);
		
		loadScalar(elseText, a);
		binaryOp(elseText, BinaryActionType.INT_DIVIDE_INT, y);
		elseText.add(new InstructionLeftShiftImmediate((short) 1));
		storeScalar(elseText, b);
		
		loadScalar(elseText, a);
		binaryOp(elseText, BinaryActionType.INT_REMAINDER_INT, y);
		elseText.add(new InstructionLeftShiftImmediate((short) 1));
		storeScalar(elseText, a);
		
		loadScalar(elseText, x);
		elseText.add(new InstructionAndImmediate((short) 1));
		binaryOp(elseText, BinaryActionType.INT_OR_INT, a);
		binaryOp(elseText, BinaryActionType.NAT_MORE_OR_EQUAL_NAT, y);
		binaryOp(elseText, BinaryActionType.INT_PLUS_INT, b);
		returnFromSubroutine(elseText);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 3;
	}
}
