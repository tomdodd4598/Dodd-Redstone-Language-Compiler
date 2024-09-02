package drlc.low.drc1.builtin;

import java.util.*;

import drlc.Global;
import drlc.intermediate.action.BinaryActionType;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.jump.*;
import drlc.low.drc1.instruction.set.InstructionSetNegative;

public class PrintIntRedstoneRoutine extends RedstoneRoutine {
	
	public PrintIntRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> setupText = new ArrayList<>(), minusText = new ArrayList<>(), callText = new ArrayList<>();
		sectionTextMap.put(0, setupText);
		sectionTextMap.put(1, minusText);
		sectionTextMap.put(2, callText);
		
		DataId x = params.get(0).dataId();
		
		loadScalar(setupText, x);
		setupText.add(new InstructionConditionalJumpIfMoreThanOrEqualToZero(2));
		
		loadImmediate(setupText, (short) '-');
		setupText.add(new InstructionOutput());
		
		loadImmediate(setupText, Short.MIN_VALUE);
		binaryOp(setupText, BinaryActionType.INT_XOR_INT, x);
		setupText.add(new InstructionConditionalJumpIfNotZero(1));
		
		loadImmediate(setupText, (short) '3');
		setupText.add(new InstructionOutput());
		loadImmediate(setupText, (short) 30000);
		binaryOp(setupText, BinaryActionType.INT_PLUS_INT, x);
		storeScalar(setupText, x);
		
		loadScalar(minusText, x);
		minusText.add(new InstructionSetNegative());
		
		builtInSubroutine(callText, Global.PRINT_DIGITS, () -> loadImmediate(callText, (short) 10000), () -> loadImmediate(callText, (short) 0));
		
		returnFromSubroutine(callText);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 3;
	}
}
