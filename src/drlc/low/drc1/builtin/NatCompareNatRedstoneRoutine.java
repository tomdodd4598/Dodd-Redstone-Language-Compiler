package drlc.low.drc1.builtin;

import java.util.*;

import drlc.Global;
import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.Instruction;

public class NatCompareNatRedstoneRoutine extends RedstoneRoutine {
	
	public NatCompareNatRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> text = new ArrayList<>();
		sectionTextMap.put(0, text);
		
		// intCompareInt(Short.MIN_VALUE + x, Short.MIN_VALUE + y)
		
		DataId x = params.get(0).dataId(), y = params.get(1).dataId();
		
		loadImmediate(text, Short.MIN_VALUE);
		binaryOp(text, BinaryActionType.INT_PLUS_INT, x);
		
		builtInSubroutine(text, Global.INT_COMPARE_INT, () -> {
			loadImmediate(text, Short.MIN_VALUE);
			binaryOp(text, BinaryActionType.INT_PLUS_INT, y);
		});
		
		returnFromSubroutine(text);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 1;
	}
}
