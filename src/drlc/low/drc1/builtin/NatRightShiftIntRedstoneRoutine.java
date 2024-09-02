package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;
import drlc.low.drc1.instruction.immediate.InstructionLeftShiftImmediate;
import drlc.low.drc1.instruction.set.InstructionSetNot;

public class NatRightShiftIntRedstoneRoutine extends RedstoneRoutine {
	
	public NatRightShiftIntRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		// (x >> y) & ~((Short.MIN_VALUE >> y) << 1)
		
		DataId x = params.get(0).dataId(), y = params.get(1).dataId();
		
		loadScalar(text, x);
		binaryOp(text, BinaryActionType.INT_RIGHT_SHIFT_INT, y);
		storeScalar(text, x);
		
		loadImmediate(text, Short.MIN_VALUE);
		binaryOp(text, BinaryActionType.INT_RIGHT_SHIFT_INT, y);
		
		text.add(new InstructionLeftShiftImmediate((short) 1));
		
		text.add(new InstructionSetNot());
		
		binaryOp(text, BinaryActionType.INT_AND_INT, x);
		
		returnFromSubroutine(text);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 1;
	}
}
