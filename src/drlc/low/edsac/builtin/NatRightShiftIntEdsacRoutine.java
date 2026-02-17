package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.*;
import drlc.low.edsac.instruction.jump.*;

public class NatRightShiftIntEdsacRoutine extends EdsacRoutine {
	
	public NatRightShiftIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> loopText = new ArrayList<>(), signText = new ArrayList<>(), continueText = new ArrayList<>(), returnText = new ArrayList<>();
		sectionTextMap.put(0, loopText);
		sectionTextMap.put(1, signText);
		sectionTextMap.put(2, continueText);
		sectionTextMap.put(3, returnText);
		
		LowDataInfo x = getDataInfo(params.get(0).dataId(), 0), y = getDataInfo(params.get(1).dataId(), 0);
		
		loopText.add(new InstructionAdd(y));
		loopText.add(new InstructionSubtract(constantInfo(1)));
		loopText.add(new InstructionJumpIfLessThanZero(3));
		loopText.add(new InstructionStoreAndClear(y));
		loopText.add(new InstructionAdd(x));
		loopText.add(new InstructionRightShift(1));
		loopText.add(new InstructionJumpIfMoreThanOrEqualToZero(2));
		
		signText.add(new InstructionSubtract(constantInfo(EdsacInt.MIN_VALUE)));
		
		continueText.add(new InstructionStoreAndClear(x));
		continueText.add(new InstructionJumpIfMoreThanOrEqualToZero(0));
		
		returnText.add(new InstructionAdd(x));
		returnFromSubroutine(returnText);
	}
}
