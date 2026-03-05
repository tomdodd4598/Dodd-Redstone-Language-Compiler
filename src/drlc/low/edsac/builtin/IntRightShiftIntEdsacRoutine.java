package drlc.low.edsac.builtin;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.low.LowDataInfo;
import drlc.low.edsac.*;
import drlc.low.edsac.instruction.*;
import drlc.low.edsac.instruction.address.InstructionSubtract;
import drlc.low.edsac.instruction.jump.*;

public class IntRightShiftIntEdsacRoutine extends EdsacRoutine {
	
	public IntRightShiftIntEdsacRoutine(EdsacCode code, Routine intermediate) {
		super(code, intermediate);
	}
	
	@Override
	protected void generateInstructionsInternal() {
		List<Instruction> loopText = new ArrayList<>(), returnText = new ArrayList<>();
		sectionTextMap.put(0, loopText);
		sectionTextMap.put(1, returnText);
		
		LowDataInfo x = getDataInfo(params.get(0).dataId(), 0), y = getDataInfo(params.get(1).dataId(), 0);
		
		addData(loopText, y);
		loopText.add(new InstructionSubtract(constantDataInfo(1)));
		loopText.add(new InstructionJumpIfLessThanZero(1));
		storeData(loopText, y, true);
		addData(loopText, x);
		loopText.add(new InstructionRightShift(1));
		storeData(loopText, x, true);
		loopText.add(new InstructionJumpIfMoreThanOrEqualToZero(0));
		
		addData(returnText, x);
		returnFromSubroutine(returnText);
	}
}
