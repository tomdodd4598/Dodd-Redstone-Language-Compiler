package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.InstructionJumpIfLessThanZero;

public class IntNotEqualToIntEdsacRoutine extends EdsacRoutine {
	
	public IntNotEqualToIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> falseText = new ArrayList<>(), trueText = new ArrayList<>();
		sectionTextMap.put(0, falseText);
		sectionTextMap.put(1, trueText);
		
		LowDataInfo x = getDataInfo(params.get(0).dataId(), 0), y = getDataInfo(params.get(1).dataId(), 0);
		
		addData(falseText, x);
		subtractData(falseText, y);
		falseText.add(new InstructionJumpIfLessThanZero(1));
		falseText.add(new InstructionRaw("TF")); // [0]
		falseText.add(new InstructionRaw("SF")); // [0]
		falseText.add(new InstructionJumpIfLessThanZero(1));
		falseText.add(new InstructionRaw("TF")); // [0]
		returnFromSubroutineIfMoreThanOrEqualToZero(falseText);
		
		trueText.add(new InstructionRaw("TF")); // [0]
		trueText.add(new InstructionSubtract(constantDataInfo(1)));
		returnFromSubroutineIfLessThanZero(trueText);
		
	}
}
