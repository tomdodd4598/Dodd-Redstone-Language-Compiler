package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.InstructionJumpIfMoreThanOrEqualToZero;

public class PrintBoolEdsacRoutine extends EdsacRoutine {
	
	public PrintBoolEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> trueText = new ArrayList<>(), falseText = new ArrayList<>();
		sectionTextMap.put(0, trueText);
		sectionTextMap.put(1, falseText);
		
		LowDataInfo x = getDataInfo(params.get(0).dataId(), 0);
		
		trueText.add(new InstructionPrint(constantDataInfo(EdsacChar.LETTER_SHIFT)));
		addData(trueText, x);
		trueText.add(new InstructionJumpIfMoreThanOrEqualToZero(1));
		"TRUE".chars().forEach(c -> trueText.add(new InstructionPrint(constantDataInfo(EdsacChar.of((char) c)))));
		returnFromSubroutineIfLessThanZero(trueText);
		
		"FALSE".chars().forEach(c -> falseText.add(new InstructionPrint(constantDataInfo(EdsacChar.of((char) c)))));
		returnFromSubroutineIfMoreThanOrEqualToZero(falseText);
	}
}
