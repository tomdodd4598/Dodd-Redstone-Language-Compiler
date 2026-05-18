package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class IntLeftRotateIntEdsacRoutine extends EdsacRoutine {
	
	public IntLeftRotateIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> loopText = new ArrayList<>(), rotateText = new ArrayList<>(), returnText = new ArrayList<>();
		sectionTextMap.put(0, loopText);
		sectionTextMap.put(1, rotateText);
		sectionTextMap.put(2, returnText);
		
		LowDataInfo x = getDataInfo(params.get(0).dataId(), 0), y = getDataInfo(params.get(1).dataId(), 0);
		
		addData(loopText, y);
		loopText.add(new InstructionSubtract(constantDataInfo(1)));
		loopText.add(new InstructionJumpIfLessThanZero(2));
		storeData(loopText, y, true);
		addData(loopText, x);
		loopText.add(new InstructionJumpIfLessThanZero(1));
		loopText.add(new InstructionLeftShift(1));
		storeData(loopText, x, true);
		loopText.add(new InstructionJumpIfMoreThanOrEqualToZero(0));
		
		rotateText.add(new InstructionLeftShift(1));
		rotateText.add(new InstructionAdd(constantDataInfo(1)));
		storeData(rotateText, x, true);
		rotateText.add(new InstructionJumpIfMoreThanOrEqualToZero(0));
		
		clearAccumulator(returnText);
		addData(returnText, x);
		returnFromSubroutine(returnText);
	}
}
