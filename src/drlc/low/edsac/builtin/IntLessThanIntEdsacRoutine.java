package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;
import drlc.low.edsac.instruction.address.InstructionSubtract;
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
		
		addData(entryText, x);
		storeData(entryText, scratchDataInfo(0), false);
		entryText.add(new InstructionJumpIfLessThanZero(1));
		clearAccumulator(entryText);
		addData(entryText, y);
		entryText.add(new InstructionJumpIfLessThanZero(3));
		entryText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		
		clearAccumulator(negativeText);
		addData(negativeText, y);
		negativeText.add(new InstructionJumpIfMoreThanOrEqualToZero(4));
		
		clearAccumulator(subtractText);
		addData(subtractText, scratchDataInfo(0));
		subtractData(subtractText, y);
		subtractText.add(new InstructionJumpIfLessThanZero(4));
		
		clearAccumulator(falseText);
		returnFromSubroutineIfMoreThanOrEqualToZero(falseText);
		
		clearAccumulator(trueText);
		trueText.add(new InstructionSubtract(constantDataInfo(1)));
		returnFromSubroutineIfLessThanZero(trueText);
	}
}
