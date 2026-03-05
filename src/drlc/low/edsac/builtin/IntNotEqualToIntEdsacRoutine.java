package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.Instruction;
import drlc.low.edsac.instruction.address.InstructionSubtract;
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
		storeData(falseText, scratchDataInfo(0), true);
		subtractData(falseText, scratchDataInfo(0));
		falseText.add(new InstructionJumpIfLessThanZero(1));
		clearAccumulator(falseText);
		returnFromSubroutineIfMoreThanOrEqualToZero(falseText);
		
		clearAccumulator(trueText);
		trueText.add(new InstructionSubtract(constantDataInfo(1)));
		returnFromSubroutineIfLessThanZero(trueText);
		
	}
}
