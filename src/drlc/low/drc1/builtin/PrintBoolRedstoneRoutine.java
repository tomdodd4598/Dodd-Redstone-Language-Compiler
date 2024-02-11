package drlc.low.drc1.builtin;

import java.util.*;

import drlc.intermediate.component.data.DataId;
import drlc.intermediate.routine.Routine;
import drlc.low.drc1.*;
import drlc.low.drc1.instruction.*;
import drlc.low.drc1.instruction.jump.InstructionConditionalJumpIfZero;

public class PrintBoolRedstoneRoutine extends RedstoneRoutine {
	
	public PrintBoolRedstoneRoutine(RedstoneCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	public void generateInstructionsInternal() {
		List<Instruction> trueText = new ArrayList<>(), falseText = new ArrayList<>();
		textSectionMap.put(0, trueText);
		textSectionMap.put(1, falseText);
		
		DataId x = params.get(0).dataId();
		
		loadScalar(trueText, x);
		trueText.add(new InstructionConditionalJumpIfZero(1));
		
		"true".chars().forEach(c -> {
			loadImmediate(trueText, (short) c);
			trueText.add(new InstructionOutput());
		});
		
		returnFromSubroutine(trueText);
		
		"false".chars().forEach(c -> {
			loadImmediate(falseText, (short) c);
			falseText.add(new InstructionOutput());
		});
		
		returnFromSubroutine(falseText);
	}
	
	@Override
	public int getFinalTextSectionKey() {
		return 2;
	}
}
