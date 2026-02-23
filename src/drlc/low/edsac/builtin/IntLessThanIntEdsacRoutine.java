package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class IntLessThanIntEdsacRoutine extends EdsacRoutine {
	
	public IntLessThanIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> entryText = new ArrayList<>(), negativeText = new ArrayList<>(), subtractText = new ArrayList<>(), falseText = new ArrayList<>(), trueText = new ArrayList<>();
		sectionTextMap.put(0, entryText);
		sectionTextMap.put(1, negativeText);
		sectionTextMap.put(2, subtractText);
		sectionTextMap.put(3, falseText);
		sectionTextMap.put(4, trueText);
		
		LowDataInfo x = getDataInfo(params.get(0).dataId(), 0), y = getDataInfo(params.get(1).dataId(), 0);
		
		entryText.add(new InstructionAdd(x));
		entryText.add(new InstructionJumpIfLessThanZero(1));
		entryText.add(new InstructionRaw("TF")); // [0]
		entryText.add(new InstructionAdd(y));
		entryText.add(new InstructionJumpIfLessThanZero(3));
		entryText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		
		negativeText.add(new InstructionRaw("TF")); // [0]
		negativeText.add(new InstructionAdd(y));
		negativeText.add(new InstructionJumpIfMoreThanOrEqualToZero(4));
		
		subtractText.add(new InstructionRaw("T1F")); // [1]
		subtractText.add(new InstructionRaw("AF")); // [0]
		subtractText.add(new InstructionSubtract(y));
		subtractText.add(new InstructionJumpIfLessThanZero(4));
		
		falseText.add(new InstructionRaw("TF")); // [0]
		returnFromSubroutineIfMoreThanOrEqualToZero(falseText);
		
		trueText.add(new InstructionRaw("TF")); // [0]
		trueText.add(new InstructionSubtract(constantInfo(1)));
		returnFromSubroutineIfLessThanZero(trueText);
	}
}
